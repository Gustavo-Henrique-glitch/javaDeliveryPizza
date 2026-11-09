TEST_SUITE     ?= all
SUITES         := $(if $(filter all,$(TEST_SUITE)),unit integration,$(TEST_SUITE))

$(if $(filter $(TEST_SUITE),unit integration all),,$(error TEST_SUITE deve ser unit, integration ou all))

JAVA_COMPILER  := javac
JAVA_RUNTIME   := java
JAVA_ENCODING  := UTF-8

SYSTEM_PATH    := ../src
BUILD_PATH     := ../build/tests
CLASSES_PATH   := $(BUILD_PATH)/classes
SYSTEM_CLASSES := $(CLASSES_PATH)/main
TEST_CLASSES   := $(CLASSES_PATH)/$(TEST_SUITE)
REPORTS_PATH   := $(BUILD_PATH)/reports
RESULTS_PATH   := $(REPORTS_PATH)/results
COVERAGE_PATH  := $(REPORTS_PATH)/coverage
COVERAGE_DATA  := $(CLASSES_PATH)/jacoco.exec

REGRESSION_VERSIONS := 2bcbd53 7b1ad64 9604624
REGRESSION_TARGETS  := $(addprefix regression_,$(REGRESSION_VERSIONS))
REGRESSION_BUILD    := $(abspath $(CLASSES_PATH)/regression)
REGRESSION_CLASSES  := $(REGRESSION_BUILD)/test
REGRESSION_REPORTS  := $(REPORTS_PATH)/regression
REGRESSION_SOURCES  := $(shell find regression -name '*.java')
REGRESSION_TABLE    := $(REGRESSION_REPORTS)/tabela.txt
REGRESSION_ROW      := %-9s %-11s %-9s %10s %9s %9s %14s\n
SUITE_SOURCES       := $(shell find helpers unit integration -name '*.java')
SUITE_TEST_COUNT    := $(shell cat $(SUITE_SOURCES) | grep -c '@Test')
COUNT_RESULTS       := sed -n 's/.*name="JUnit Jupiter" tests="\([0-9]*\)" skipped="[0-9]*" failures="\([0-9]*\)".*/\1 \2/p'
JUNIT_EXECUTE       := -jar $(JUNIT_JAR) execute --include-classname '.*Teste' --details=tree

MAIN_CLASS_2bcbd53  := javaDeliveryPizza.app.Main
MAIN_CLASS_7b1ad64  := javaDeliveryPizza.app.Main
MAIN_CLASS_9604624  := javaDeliveryPizza.Main

SYSTEM_SOURCES := $(shell find $(SYSTEM_PATH) -name '*.java')
TEST_SOURCES   := $(shell find helpers $(SUITES) -name '*.java')

RUN_TESTS := $(JAVA_RUNTIME) -javaagent:$(JACOCO_AGENT_JAR)=destfile=$(COVERAGE_DATA),append=false \
	-Dfile.encoding=$(JAVA_ENCODING) -jar $(JUNIT_JAR) execute \
	--class-path $(SYSTEM_CLASSES):$(TEST_CLASSES) --scan-classpath $(TEST_CLASSES) \
	--include-classname '.*Teste' --details=tree --reports-dir $(RESULTS_PATH)

REPORT_COVERAGE := $(JAVA_RUNTIME) -jar $(JACOCO_CLI_JAR) report $(COVERAGE_DATA) \
	--classfiles $(SYSTEM_CLASSES) --sourcefiles $(SYSTEM_PATH) \
	--html $(COVERAGE_PATH) --xml $(COVERAGE_PATH)/jacoco.xml --quiet

.PHONY: help test compile regression compile_regression $(REGRESSION_TARGETS)

help:
	@echo "-- [$@] --"
	@echo "make help        mostra esta ajuda"
	@echo "make compile     compila o sistema e os testes"
	@echo "make test        compila e executa os testes"
	@echo "                 TEST_SUITE=unit|integration|all"
	@echo "make regression  executa a regressao em $(REGRESSION_VERSIONS)"

test: compile
	@echo "-- [$@] --"
	$(RUN_TESTS); tests_status=$$?; $(REPORT_COVERAGE) && exit $$tests_status

compile:
	@echo "-- [$@] --"
	mkdir -p $(COVERAGE_PATH)
	$(JAVA_COMPILER) -encoding $(JAVA_ENCODING) -d $(SYSTEM_CLASSES) $(SYSTEM_SOURCES)
	$(JAVA_COMPILER) -encoding $(JAVA_ENCODING) -d $(TEST_CLASSES) -cp $(SYSTEM_CLASSES):$(JUNIT_JAR) $(TEST_SOURCES)

regression: $(REGRESSION_TARGETS)
	@echo "-- [$@] --"
	@printf '$(REGRESSION_ROW)' versao data suite executados passaram falharam incompativeis | tee $(REGRESSION_TABLE)
	@for version in $(REGRESSION_VERSIONS); do \
		date=$$(git -C .. log -1 --format=%ad --date=format:%d/%m/%Y $$version); \
		set -- $$($(COUNT_RESULTS) $(REGRESSION_REPORTS)/$$version/TEST-junit-jupiter.xml); \
		printf '$(REGRESSION_ROW)' $$version $$date terminal $$1 $$(($$1-$$2)) $$2 0; \
		if [ -f $(REGRESSION_REPORTS)/$$version/suite/TEST-junit-jupiter.xml ]; then \
			set -- $$($(COUNT_RESULTS) $(REGRESSION_REPORTS)/$$version/suite/TEST-junit-jupiter.xml); \
			printf '$(REGRESSION_ROW)' $$version $$date completa $$1 $$(($$1-$$2)) $$2 0; \
		else \
			printf '$(REGRESSION_ROW)' $$version $$date completa 0 0 0 $(SUITE_TEST_COUNT); \
		fi; \
	done | tee -a $(REGRESSION_TABLE)

compile_regression:
	@echo "-- [$@] --"
	$(JAVA_COMPILER) -encoding $(JAVA_ENCODING) -d $(REGRESSION_CLASSES) -cp $(JUNIT_JAR) $(REGRESSION_SOURCES)

$(REGRESSION_TARGETS): regression_%: compile_regression
	@echo "-- [$@] --"
	mkdir -p $(REGRESSION_BUILD)/$*
	git -C .. archive $* src | tar -x -C $(REGRESSION_BUILD)/$*
	$(JAVA_COMPILER) -nowarn -encoding $(JAVA_ENCODING) -d $(REGRESSION_BUILD)/$*/classes $$(find $(REGRESSION_BUILD)/$*/src -name '*.java')
	mkdir -p $(REGRESSION_REPORTS)/$*
	-$(JAVA_RUNTIME) -Dsistema.classes=$(REGRESSION_BUILD)/$*/classes -Dsistema.principal=$(MAIN_CLASS_$*) $(JUNIT_EXECUTE) \
		--class-path $(REGRESSION_CLASSES) --scan-classpath $(REGRESSION_CLASSES) --reports-dir $(REGRESSION_REPORTS)/$*
	-$(JAVA_COMPILER) -nowarn -encoding $(JAVA_ENCODING) -d $(REGRESSION_BUILD)/$*/suite \
		-cp $(REGRESSION_BUILD)/$*/classes:$(JUNIT_JAR) $(SUITE_SOURCES) 2> $(REGRESSION_REPORTS)/$*/compilacao.log \
		&& $(JAVA_RUNTIME) $(JUNIT_EXECUTE) --class-path $(REGRESSION_BUILD)/$*/classes:$(REGRESSION_BUILD)/$*/suite \
		--scan-classpath $(REGRESSION_BUILD)/$*/suite --reports-dir $(REGRESSION_REPORTS)/$*/suite
