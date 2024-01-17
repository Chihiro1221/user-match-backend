# 基于官方提供的Maven OpenJDK镜像作为基础镜像
FROM maven:3.6.3-openjdk-17 as build

# 设置工作目录
WORKDIR /app

# 将本地Maven项目复制到容器中的/app目录下
COPY pom.xml .
COPY src ./src

# 使用Maven打包
RUN mvn package -DskipTests

# 开始构建新阶段
FROM openjdk:17-jdk

# 设置工作目录
WORKDIR /app

#从前一个阶段复制打包后的jar包到当前阶段
COPY --from=build /app/target/user-match-0.0.1-SNAPSHOT.jar .
# 暴露端口
EXPOSE 8080

# Run the web service on container startup.
CMD ["java","-jar","/app/user-match-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]