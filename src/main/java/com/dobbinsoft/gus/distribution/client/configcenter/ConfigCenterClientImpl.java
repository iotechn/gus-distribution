package com.dobbinsoft.gus.distribution.client.configcenter;

import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.context.bo.TenantContext;
import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.client.configcenter.vo.ConfigContentVO;
import com.dobbinsoft.gus.distribution.data.properties.DistributionProperties;
import com.dobbinsoft.gus.distribution.utils.AESUtil;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class ConfigCenterClientImpl implements ConfigCenterClient {

    @Autowired
    private DistributionProperties distributionProperties;

    private static final String CONFIG_KEY_PREFIX = "distribution:config:tenant:";
    private static final Duration CACHE_EXPIRE_TIME = Duration.ofDays(30); // 配置缓存30天

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(ConfigContentVO configContentVO) {
        try {
            String tenantId = getTenantId();
            String cacheKey = buildCacheKey(tenantId);
            
            // 对secret配置进行AES加密
            ConfigContentVO encryptedConfig = encryptSecretConfig(configContentVO);
            
            // 序列化为JSON字符串
            String configJson = JsonUtil.convertToString(encryptedConfig);
            
            // 保存配置到Redis
            stringRedisTemplate.opsForValue().set(cacheKey, configJson, CACHE_EXPIRE_TIME);
            
            log.info("配置保存成功，租户ID: {}, 缓存Key: {}", tenantId, cacheKey);
        } catch (Exception e) {
            log.error("保存配置失败", e);
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "保存配置失败: " + e.getMessage());
        }
    }

    @Override
    public ConfigContentVO getBrandAllConfigContent() {
        try {
            String tenantId = getTenantId();
            String cacheKey = buildCacheKey(tenantId);
            
            // 从Redis读取配置JSON字符串
            String configJson = stringRedisTemplate.opsForValue().get(cacheKey);

            ConfigContentVO config;
            if (configJson == null) {
                log.info("配置缓存为空，初始化默认配置，租户ID: {}", tenantId);
                config = createDefaultConfig();
                // 保存默认配置到Redis
                save(config);
            } else {
                // 反序列化JSON & 解密secret配置
                config = decryptSecretConfig(JsonUtil.convertValue(configJson, ConfigContentVO.class));
            }
            
            return config;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取配置失败", e);
            // 如果Redis读取失败，返回默认配置
            return createDefaultConfig();
        }
    }


    /**
     * 构建缓存Key，确保租户隔离
     */
    private String buildCacheKey(String tenantId) {
        return CONFIG_KEY_PREFIX + tenantId;
    }

    /**
     * 加密secret配置
     */
    private ConfigContentVO encryptSecretConfig(ConfigContentVO config) {
        if (config == null || config.getSecret() == null) {
            return config;
        }

        try {
            ConfigContentVO encryptedConfig = JsonUtil.convertValue(JsonUtil.convertToString(config), ConfigContentVO.class);
            ConfigContentVO.Secret secret = encryptedConfig.getSecret();

            if (secret.getWechatMiniAppId() != null) {
                secret.setWechatMiniAppId(AESUtil.encrypt(secret.getWechatMiniAppId(), distributionProperties.getAesKey()));
            }
            if (secret.getWechatMiniSecret() != null) {
                secret.setWechatMiniSecret(AESUtil.encrypt(secret.getWechatMiniSecret(), distributionProperties.getAesKey()));
            }

            return encryptedConfig;
        } catch (Exception e) {
            log.error("加密secret配置失败", e);
            return config; // 加密失败时返回原配置
        }
    }

    /**
     * 解密secret配置
     */
    private ConfigContentVO decryptSecretConfig(ConfigContentVO config) {
        if (config == null || config.getSecret() == null) {
            return config;
        }

        try {
            ConfigContentVO.Secret secret = config.getSecret();

            if (secret.getWechatMiniAppId() != null) {
                secret.setWechatMiniAppId(AESUtil.decrypt(secret.getWechatMiniAppId(), distributionProperties.getAesKey()));
            }
            if (secret.getWechatMiniSecret() != null) {
                secret.setWechatMiniSecret(AESUtil.decrypt(secret.getWechatMiniSecret(), distributionProperties.getAesKey()));
            }
            return config;
        } catch (Exception e) {
            log.error("解密secret配置失败", e);
            return config; // 解密失败时返回原配置
        }
    }


    /**
     * 创建默认配置
     */
    private ConfigContentVO createDefaultConfig() {
        ConfigContentVO config = new ConfigContentVO();

        // 初始化密钥配置
        config.setSecret(new ConfigContentVO.Secret());

        // 文案/图片类默认置空，由上层自行配置
        config.setMpTitle(null);
        config.setWxShareTitle(null);
        config.setWxShareImg(null);
        config.setLogo(null);

        // 超时配置：单位秒
        config.setAutoCancelTime(15 * 60);       // 15 分钟
        config.setAutoConfirmTime(3 * 24 * 60 * 60); // 3 天

        // 商家信息默认空
        config.setMerchantPhone(null);
        config.setMerchantWxQr(null);

        // 功能开关默认值
        config.setEnableZeroPay(false);
        config.setEnableLocationChoose(true);
        config.setEnableVirtualLocation(true);
        config.setEnableOutRangeOrder(true);
        config.setEnableCartSumSku(true);

        // 主题颜色
        config.setThemeColor("green_theme");

        // 小程序订单同步默认开启
        config.setEnableMpOrderSync(true);

        return config;
    }

    public String getTenantId() {
        return GenericRequestContextHolder.getTenantContext()
                .map(TenantContext::getTenantId)
                .orElseThrow(() -> new ServiceException(BasicErrorCode.SYSTEM_ERROR));
    }
}
