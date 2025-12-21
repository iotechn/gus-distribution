FROM registry.cn-beijing.aliyuncs.com/dobbinsoft/ubuntu:25.10

COPY ./target/${REPO_NAME} /app/${REPO_NAME}

# 添加执行权限
RUN chmod +x /app/${REPO_NAME}

EXPOSE 8080

CMD ["/app/${REPO_NAME}"]