# ----------------------------------------------------------
# Magnolia Docker Image
# ----------------------------------------------------------
#
FROM tomcat:9.0.38-jdk11-openjdk-slim

LABEL maintainer="Formentor Studio"

# ENV variables for Magnolia
ENV MGNL_VERSION 6.2
ENV MGNL_APP_DIR /opt/magnolia/data
ENV MGNL_RESOURCES_DIR=/opt/magnolia/light-modules

# ARGS
ARG MGNL_WAR_PATH=magnolia-ai-bundle-webapp/target/ROOT.war
ARG MGNL_HEAP=2048M

ENV CATALINA_OPTS -server \
    -Djava.security.egd=file:/dev/./urandom \
    -Xms64M -Xmx${MGNL_HEAP} -Djava.awt.headless=true \
    -Dmagnolia.home=${MGNL_APP_DIR} \
    -Dmagnolia.author.key.location=${MGNL_APP_DIR}/magnolia-activation-keypair.properties \
    -Dmagnolia.resources.dir=${MGNL_RESOURCES_DIR} \
    -Dmagnolia.develop=true \
    -Dmagnolia.bootstrap.authorInstance=true \
    -Dmagnolia.update.auto=true

# VOLUME for Magnolia
VOLUME [ "${MGNL_APP_DIR}" ]

# MGNL war
COPY ${MGNL_WAR_PATH} $CATALINA_HOME/webapps/ROOT.war
