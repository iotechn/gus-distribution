package com.dobbinsoft.gus.distribution.client.erp;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;

import com.dobbinsoft.gus.common.utils.context.GenericRequestContextHolder;
import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpCategory;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItem;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItemAttr;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpItemMapping;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpStock;
import com.dobbinsoft.gus.distribution.client.erp.model.ErpUnitGroup;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpAccessTokenData;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpApiResponse;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpCategoryItem;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpConfigModel;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpInventoryItem;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpProductItem;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyErpUnitItem;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyInventoryData;
import com.dobbinsoft.gus.distribution.client.erp.model.jdy.JdyInventoryItem;
import com.dobbinsoft.gus.distribution.client.gus.product.model.ItemStatus;
import com.dobbinsoft.gus.distribution.data.po.ErpProviderPO;
import com.dobbinsoft.gus.distribution.data.properties.ErpProperties;
import com.dobbinsoft.gus.distribution.utils.json.JsonFlattener;
import com.dobbinsoft.gus.web.exception.BasicErrorCode;
import com.dobbinsoft.gus.web.exception.ServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 金蝶云ERP客户端实现
 */
@Component
@Slf4j
public class ErpClientJdyImpl implements ErpClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ErpProperties erpProperties;

    /**
     * 查询产品
     */
    public JdyErpApiResponse<List<JdyErpProductItem>> queryProduct(Integer pageSize, Integer page, String smc) throws ServiceException {
        String apiPath = "/jdyscm/product/list";
        String requestBody;
        if (StringUtils.isEmpty(smc)) {
            requestBody = "{\"filter\":{\"page\":\"%s\",\"pageSize\":\"%s\"}}".formatted(page.toString(), pageSize.toString());
        } else {
            requestBody = "{\"filter\":{\"page\":\"%s\",\"pageSize\":\"%s\",\"productNumber\":\"%s\"}}".formatted(page.toString(), pageSize.toString(), smc);
        }

        String url = String.format("%s%s", erpProperties.getJdyApiUrl(), apiPath);
        String json = doPost(url, requestBody);
        log.info("queryProduct, param:{}, response:{}", requestBody, json);

        JdyErpApiResponse<List<JdyErpProductItem>> result = JsonUtil.convertValue(json, new TypeReference<>() {
        });

        if (!result.isSuccess()) {
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, result.getMsg());
        }

        return result;
    }

    /**
     * 查询库存
     */
    public List<JdyErpInventoryItem> queryInventory(String productNumber, String locationNumber) throws ServiceException {
        // 这里需要根据实际的ERP API来实现库存查询
        // 由于原代码中没有具体的实现，这里提供一个框架
        log.info("Querying inventory for product: {}, location: {}", productNumber, locationNumber);

        // TODO: 实现具体的库存查询逻辑
        return new ArrayList<>();
    }

    @Override
    public List<ErpCategory> getCategories(ErpProviderPO erpProviderPO) {
        JdyErpConfigModel jdyErpConfigModel = erpProviderPO.asJdyErpConfigModel();
        Integer page = 1;
        Integer totalPage = 1;
        Integer pageSize = 10;
        List<JdyErpCategoryItem> allCategoryItems = new ArrayList<>();

        while (page <= totalPage) {
            String apiPath = "/jdyscm/productCategory/list";
            String apiParam = String.format("access_token=%s&dbId=%s&page=%s&pageSize=%s",
                    getAccessToken(erpProviderPO), jdyErpConfigModel.getAccountId(), page, pageSize);
            String url = String.format("%s%s?%s", erpProperties.getJdyApiUrl(), apiPath, apiParam);

            String json = doGet(url);
            log.info("queryCategory, page:{}, pageSize:{}, response:{}", page, pageSize, json);

            JdyErpApiResponse<List<JdyErpCategoryItem>> result = JsonUtil.convertValue(json, new TypeReference<>() {
            });
            if (!result.isSuccess()) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, result.getMsg());
            }
            totalPage = result.getTotalPages();
            page++;

            if (result.getData() == null || result.getData().isEmpty()) {
                continue;
            }
            allCategoryItems.addAll(result.getData());
        }
        return allCategoryItems.stream().map(JdyErpCategoryItem::toStandard).toList();
    }

    @Override
    public List<ErpUnitGroup> getUnits(ErpProviderPO erpProviderPO) {
        JdyErpConfigModel jdyErpConfigModel = erpProviderPO.asJdyErpConfigModel();
        int page = 1;
        boolean hasNext = true;
        List<JdyErpUnitItem> units = new ArrayList<>();

        do {
            String apiPath = "/jdyscm/unit/list";
            String requestBody = "{\"filter\":{\"page\":\"%s\",\"type\":1}}".formatted(page + "");

            String url = String.format("%s%s", erpProperties.getJdyApiUrl(), apiPath);
            String json = doPost(url, requestBody);
            log.info("queryAllUnits, page:{}, hasNext:{}, response: {}", page, hasNext, json);
            JdyErpApiResponse<List<JdyErpUnitItem>> result = JsonUtil.convertValue(json, new TypeReference<>() {});
            if (!result.isSuccess()) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, result.getMsg());
            }

            if (result.getData() != null && !result.getData().isEmpty()) {
                units.addAll(result.getData());
                if (result.getTotalPages() != null && result.getTotalPages() <= page) {
                    hasNext = false;
                }
            } else {
                hasNext = false;
            }

            page++;
        } while (hasNext);
        return units.stream().map(JdyErpUnitItem::toStandard).collect(Collectors.toList());
    }

    @Override
    public ErpItemAttr getItemAttr(String smc, ErpProviderPO erpProviderPO) {
        ErpItemAttr erpItemAttr = new ErpItemAttr();
        erpItemAttr.setDocumentUrl("https://open.jdy.com/#/files/api/detail?index=3&categrayId=a125933242c3454190877377b96b8b14&id=37084997dfc74aa3b037e77502f672fe");
        JdyErpApiResponse<List<JdyErpProductItem>> listJdyErpApiResponse = queryProduct(1, 1, smc);
        List<JdyErpProductItem> data = listJdyErpApiResponse.getData();
        if (CollectionUtils.isEmpty(data)) {
            erpItemAttr.setDemoJson("{}");
            erpItemAttr.setAttrs(new ArrayList<>());
            return erpItemAttr;
        }
        JdyErpProductItem first = data.getFirst();
        erpItemAttr.setDemoJson(JsonUtil.convertToString(first));
        JsonNode jsonNode = JsonUtil.convertToObject(erpItemAttr.getDemoJson(), JsonNode.class);
        ObjectNode flatten = JsonFlattener.flatten(jsonNode);

        erpItemAttr.setAttrs(new ArrayList<>());

        Iterator<String> names = flatten.fieldNames();
        names.forEachRemaining(name -> {
            JsonNode node = flatten.get(name);
            if (node.isNull()) {
                // 忽略空值
                return;
            }
            ErpItemAttr.Attr attr = new ErpItemAttr.Attr();
            attr.setName(name);
            if (node.isBoolean()) {
                attr.setDataType(ErpItemAttr.DataType.BOOLEAN);
                attr.setDemoValue(node.asBoolean());
            } else if (node.isInt()) {
                attr.setDataType(ErpItemAttr.DataType.INTEGER);
                attr.setDemoValue(node.asInt());
            } else if (node.isBigDecimal()) {
                attr.setDataType(ErpItemAttr.DataType.BIG_DECIMAL);
                attr.setDemoValue(new BigDecimal(node.asText()));
            } else if (node.isTextual()) {
                attr.setDataType(ErpItemAttr.DataType.STRING);
                attr.setDemoValue(node.asText());
            } else if (node.isArray()) {
                Iterator<JsonNode> elements = node.elements();
                attr.setDataType(ErpItemAttr.DataType.ARRAY_UNKNOWN);
                List<Object> list = new ArrayList<>();
                while (elements.hasNext()) {
                    JsonNode next = elements.next();
                    if (next.isBoolean()) {
                        attr.setDataType(ErpItemAttr.DataType.ARRAY_BOOLEAN);
                        list.add(next.booleanValue());
                    } else if (next.isInt()) {
                        attr.setDataType(ErpItemAttr.DataType.ARRAY_INTEGER);
                        list.add(next.intValue());
                    } else if (next.isBigDecimal()) {
                        attr.setDataType(ErpItemAttr.DataType.ARRAY_BIG_DECIMAL);
                        list.add(new BigDecimal(next.asText()));
                    } else if (next.isTextual()) {
                        attr.setDataType(ErpItemAttr.DataType.ARRAY_STRING);
                        list.add(next.asText());
                    }
                }
                attr.setDemoValue(list);
            }
            erpItemAttr.getAttrs().add(attr);
        });
        return erpItemAttr;
    }

    @Override
    public List<ErpItem> getItems(List<ErpItemMapping> mappings, ErpProviderPO erpProviderPO) {
        Integer page = 1;
        Integer totalPage = 1;
        Integer pageSize = 50;
        List<JdyErpProductItem> jdyItems = new ArrayList<>();
        while (page <= totalPage) {
            JdyErpApiResponse<List<JdyErpProductItem>> result = queryProduct(pageSize, page, null);
            totalPage = result.getTotalPages();
            page++;

            if (result.getData() == null || result.getData().isEmpty()) {
                continue;
            }

            jdyItems.addAll(result.getData());
        }

        return jdyItems.stream().map(jdyItem -> {
            ErpItem erpItem = new ErpItem();
            erpItem.setErpId(jdyItem.getId().toString());
            erpItem.setSmc(jdyItem.getProductNumber());
            erpItem.setErpUnitGroupId(jdyItem.getUnit().toString());
            if (jdyItem.getCategoryId() != null && jdyItem.getCategoryId() > 0) {
                erpItem.setErpCategoryIds(List.of(jdyItem.getCategoryId().toString()));
            } else {
                erpItem.setErpCategoryIds(List.of());
            }
            List<JdyErpProductItem.ErpProductImage> multiImg = jdyItem.getMultiImg();
            List<String> images = new ArrayList<>();
            if (!CollectionUtils.isEmpty(multiImg)) {
                for (JdyErpProductItem.ErpProductImage erpProductImage : multiImg) {
                    // TODO 转存至minio，考虑在外侧转存
                    images.add(erpProductImage.getUrl());
                }
            }
            erpItem.setImages(images);
            erpItem.setStatus(Boolean.TRUE.equals(jdyItem.getIsDeleted()) ? ItemStatus.DISABLED : ItemStatus.ENABLED);
            // 可自定义字段的默认值
            erpItem.setName(jdyItem.getProductName());

            ObjectNode flatten = JsonFlattener.flatten(JsonUtil.convertValue(jdyItem, JsonNode.class));
            // 根据配置覆盖
            for (ErpItemMapping mapping : mappings) {
                String erpFieldKey = mapping.getErpFieldKey();
                String itemFieldKey = mapping.getItemFieldKey();
                Field field = ReflectionUtils.findField(ErpItem.class, itemFieldKey);
                if (field == null) {
                    continue;
                }
                JsonNode jsonNode = flatten.get(erpFieldKey);
                if (jsonNode.isNull()) {
                    continue;
                }
                String text = jsonNode.asText();
                Class<?> type = field.getType();
                if (type == String.class) {
                    field.setAccessible(true);
                    try {
                        field.set(erpItem, text);
                    } catch (IllegalAccessException ignored) {
                    }
                } else if (type == BigDecimal.class) {
                    field.setAccessible(true);
                    try {
                        field.set(erpItem, new BigDecimal(text));
                    } catch (IllegalAccessException ignored) {
                    }
                }
            }
            return erpItem;
        }).toList();
    }

    @Override
    public List<ErpStock> getStocks(String sku, String locationCode, ErpProviderPO erpProviderPO) {
        int page = 1;
        int pageSize = 100;
        boolean hasNext = true;
        List<ErpStock> result = new ArrayList<>();
        do {
            StringBuilder paramBuilder = new StringBuilder();
            if (!Strings.isEmpty(sku)) {
                paramBuilder.append("number=").append(sku);
            }
            if (!Strings.isEmpty(locationCode)) {
                if (!paramBuilder.isEmpty()) {
                    paramBuilder.append("&");
                }
                paramBuilder.append("location=").append(locationCode);
            }
            if (!paramBuilder.isEmpty()) {
                paramBuilder.append("&");
            }
            paramBuilder
                    .append("showZero=1")
                    .append("&page=")
                    .append(page)
                    .append("&pageSize=")
                    .append(pageSize);

            String url = String.format("%s%s?%s", erpProperties.getJdyApiUrl(), "/jdyscm/inventory/list", paramBuilder);
            String responseJson = doGet(url);
            JdyInventoryData jdyInventoryData = JsonUtil.convertToObject(responseJson, JdyInventoryData.class);
            List<JdyInventoryItem> items = jdyInventoryData.getItems();
            if (jdyInventoryData.getTotalPages() <= page) {
                hasNext = false;
            }
            List<ErpStock> erpStocks = items.stream().map(item -> {
                ErpStock erpStock = new ErpStock();
                erpStock.setSku(item.getProductNumber());
                erpStock.setLocationCode(item.getLocationNumber());
                erpStock.setQuantity(item.getQty());
                return erpStock;
            }).toList();
            result.addAll(erpStocks);
        } while (hasNext);

        return result;
    }


    /** ========================== For Request ========================*/
    private String doGet(String url) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private String doPost(String url, String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }



    /** ========================= For signature =========================== */

    /**
     * 计算签名
     */
    public static String computeSignature(String secret, String input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            // 执行HMAC计算
            byte[] hmacResult = mac.doFinal(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hmacResult) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return Base64.getEncoder().encodeToString(hexString.toString().getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取访问令牌
     */
    private String getAccessToken(ErpProviderPO erpProviderPO) {
        JdyErpConfigModel jdyErpConfigModel = erpProviderPO.asJdyErpConfigModel();
        String cacheKey = "ERP_JDY_ACCESS_CACHE_KEY:" + GenericRequestContextHolder.getTenantContext().get().getTenantId();

        String accessToken = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }

        try {
            String appSignature = computeSignature(jdyErpConfigModel.getSecret(), jdyErpConfigModel.getKey());
            String apiPath = "/jdyconnector/app_management/kingdee_auth_token";
            String apiParam = String.format("app_key=%s&app_signature=%s",
                    URLEncoder.encode(jdyErpConfigModel.getKey(), StandardCharsets.UTF_8),
                    URLEncoder.encode(appSignature, StandardCharsets.UTF_8)
            );
            String apiParamEncoded = String.format("app_key=%s&app_signature=%s",
                    URLEncoder.encode(jdyErpConfigModel.getKey(), StandardCharsets.UTF_8),
                    URLEncoder.encode(URLEncoder.encode(appSignature, StandardCharsets.UTF_8), StandardCharsets.UTF_8)
            );

            String url = String.format("%s%s?%s", erpProperties.getJdyApiUrl(), apiPath, apiParam);
            long timestamp = Instant.now().toEpochMilli();
            Random random = new Random();
            int nonce = Math.abs(random.nextInt());

            String apiSignatureData = String.format("%s\n%s\n%s\n%s\n%s\n",
                    "GET",
                    URLEncoder.encode(apiPath, StandardCharsets.UTF_8),
                    apiParamEncoded,
                    String.format("x-api-nonce:%s", nonce),
                    String.format("x-api-timestamp:%s", timestamp)
            );
            String apiSignature = computeSignature(jdyErpConfigModel.getClientSecret(), apiSignatureData);

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Api-ClientID", jdyErpConfigModel.getClientId());
            headers.set("X-Api-Auth-Version", "2.0");
            headers.set("X-Api-TimeStamp", String.valueOf(timestamp));
            headers.set("X-Api-SignHeaders", "X-Api-TimeStamp,X-Api-Nonce");
            headers.set("X-Api-Nonce", String.valueOf(nonce));
            headers.set("X-Api-Signature", apiSignature);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();

            JdyErpApiResponse<JdyErpAccessTokenData> result = JsonUtil.convertValue(json, new TypeReference<JdyErpApiResponse<JdyErpAccessTokenData>>() {});

            if (!result.isSuccess()) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, result.getMsg());
            }
            if (result.getData() == null) {
                throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "Access token data is null");
            }

            accessToken = result.getData().getAccessToken();
            int expire = (int) (result.getData().getExpires() * 4 / 5 / 1000);

            // 缓存访问令牌
            stringRedisTemplate.opsForValue().set(cacheKey, accessToken, Duration.ofSeconds(expire));

            return accessToken;
        } catch (Exception ex) {
            log.error("获取ERP访问令牌失败: {}", ex.getMessage(), ex);
            throw new ServiceException(BasicErrorCode.SYSTEM_ERROR, "Failed to get ERP access token: " + ex.getMessage());
        }
    }

    @Override
    public boolean validateCallback(HttpServletRequest request, String body, ErpProviderPO erpProviderPO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validateCallback'");
    }

}
