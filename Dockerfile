FROM registry.cn-beijing.aliyuncs.com/dobbinsoft/dobbinjdk:21

COPY ./target/${REPO_NAME}-0.0.1-SNAPSHOT.jar /app/${REPO_NAME}-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENV JAVA_OPTS="\
-server \
-XX:+IgnoreUnrecognizedVMOptions \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:+PrintGCDetails \
-XX:+PrintGCDateStamps \
-XX:GCLogFileSize=10M \
-XX:+PerfDisableSharedMem \
-XX:+UseCondCardMark \
-XX:+UseCMSInitiatingOccupancyOnly \
-XX:+ExitOnOutOfMemoryError"

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/${REPO_NAME}-0.0.1-SNAPSHOT.jar" ]
