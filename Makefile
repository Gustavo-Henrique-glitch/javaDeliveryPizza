PROJECT_NAME      := javaDeliveryPizza

JAVA_COMPILER     := javac
JAVA_RUNTIME      := java
JAVA_ARCHIVER     := jar
JAVA_TARGET_FLAGS := --release 17 -encoding UTF-8

SRC_PATH          := src
SRC_FILES         := $(shell find $(SRC_PATH) -name '*.java')
BUILD_PATH        := build
JAVA_BUILD_PATH   := $(BUILD_PATH)/java
CLASSES_PATH      := $(JAVA_BUILD_PATH)/classes
MAIN_CLASS        := $(PROJECT_NAME).app.Main

TARGET_NAME       := $(PROJECT_NAME)
TARGET_PATH       := $(JAVA_BUILD_PATH)/$(TARGET_NAME).jar

TESTS_PATH        := tests

MAVEN_URL         := https://repo1.maven.org/maven2
TEST_DEPS_PATH    := $(abspath $(BUILD_PATH)/tests/.dependencies)
JUNIT_VERSION     := 1.13.4
JACOCO_VERSION    := 0.8.13
JUNIT_JAR         := $(TEST_DEPS_PATH)/junit-platform-console-standalone-$(JUNIT_VERSION).jar
JACOCO_AGENT_JAR  := $(TEST_DEPS_PATH)/org.jacoco.agent-$(JACOCO_VERSION)-runtime.jar
JACOCO_CLI_JAR    := $(TEST_DEPS_PATH)/org.jacoco.cli-$(JACOCO_VERSION)-nodeps.jar
JAVA_DEPS         := $(JUNIT_JAR) $(JACOCO_AGENT_JAR) $(JACOCO_CLI_JAR)

NPM_INSTALL       := npm ci --ignore-scripts
FRONT_PATH        := front-delivery
ROOT_NODE_DEPS    := node_modules
FRONT_NODE_DEPS   := $(FRONT_PATH)/node_modules

.PHONY: help collect_dependencies collect_dev_dependencies build run test regression
.DELETE_ON_ERROR:

export JUNIT_JAR JACOCO_AGENT_JAR JACOCO_CLI_JAR

help:
	@echo "-- [$@] --"
	@echo "make help                      mostra esta ajuda"
	@echo "make collect_dependencies      coleta as dependencias npm"
	@echo "make collect_dev_dependencies  coleta JUnit e JaCoCo"
	@echo "make build                     gera $(TARGET_PATH)"
	@echo "make run                       compila e executa $(PROJECT_NAME)"
	@echo "make test                      executa os testes"
	@echo "                               TEST_SUITE=unit|integration|all"
	@echo "make regression                executa a regressao nos commits definidos"

collect_dependencies: $(ROOT_NODE_DEPS) $(FRONT_NODE_DEPS)
	@echo "-- [$@] --"

collect_dev_dependencies: $(JAVA_DEPS)
	@echo "-- [$@] --"

build:
	@echo "-- [$@] --"
	$(JAVA_COMPILER) $(JAVA_TARGET_FLAGS) -d $(CLASSES_PATH) $(SRC_FILES)
	$(JAVA_ARCHIVER) --create --file $(TARGET_PATH) --main-class $(MAIN_CLASS) -C $(CLASSES_PATH) .

run: build
	@echo "-- [$@] --"
	$(JAVA_RUNTIME) -jar $(TARGET_PATH)

test: collect_dev_dependencies
	@echo "-- [$@] --"
	$(MAKE) -C $(TESTS_PATH) test

regression: collect_dev_dependencies
	@echo "-- [$@] --"
	$(MAKE) -C $(TESTS_PATH) regression

$(JUNIT_JAR):        MAVEN_PATH := org/junit/platform/junit-platform-console-standalone/$(JUNIT_VERSION)
$(JACOCO_AGENT_JAR): MAVEN_PATH := org/jacoco/org.jacoco.agent/$(JACOCO_VERSION)
$(JACOCO_CLI_JAR):   MAVEN_PATH := org/jacoco/org.jacoco.cli/$(JACOCO_VERSION)

$(JAVA_DEPS):
	mkdir -p $(TEST_DEPS_PATH)
	curl -fsSL -o $@ $(MAVEN_URL)/$(MAVEN_PATH)/$(@F)
	echo "$$(curl -fsSL $(MAVEN_URL)/$(MAVEN_PATH)/$(@F).sha1)  $@" | sha1sum -c --status

$(ROOT_NODE_DEPS): package-lock.json
	$(NPM_INSTALL)

$(FRONT_NODE_DEPS): $(FRONT_PATH)/package-lock.json
	$(NPM_INSTALL) --prefix $(FRONT_PATH)
