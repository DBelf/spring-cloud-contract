/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.contract.stubrunner.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;
import org.springframework.boot.test.autoconfigure.properties.SkipPropertyMapping;
import org.springframework.cloud.contract.stubrunner.HttpServerStubConfigurer;
import org.springframework.cloud.contract.stubrunner.HttpServerStubConfigurer.NoOpHttpServerStubConfigurer;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;

/**
 * @author Dave Syer
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ImportAutoConfiguration
@AutoConfigureMessageVerifier
@PropertyMapping(value = "stubrunner", skip = SkipPropertyMapping.ON_DEFAULT_VALUE)
public @interface AutoConfigureStubRunner {

	/**
	 * @return Min value of a port for the automatically started WireMock server.
	 */
	int minPort() default 10000;

	/**
	 * @return Max value of a port for the automatically started WireMock server.
	 */
	int maxPort() default 15000;

	/**
	 * @return The repository root to use (where the stubs should be downloaded from).
	 */
	String repositoryRoot() default "";

	/**
	 * @return The ids of the stubs to run in "ivy" notation
	 * ([groupId]:artifactId[:version][:classifier][:port]). {@code groupId},
	 * {@code version}, {@code classifier} and {@code port} can be optional.
	 */
	String[] ids() default {};

	/**
	 * @return The classifier to use by default in ivy co-ordinates for a stub.
	 */
	String classifier() default "stubs";

	/**
	 * On the producer side the consumers can have a folder that contains contracts
	 * related only to them. By setting the flag to {@code true} we no longer register all
	 * stubs but only those that correspond to the consumer application's name. In other
	 * words we'll scan the path of every stub and if it contains the name of the consumer
	 * in the path only then will it get registered.
	 *
	 * Let's look at this example. Let's assume that we have a producer called {@code foo}
	 * and two consumers {@code baz} and {@code bar}. On the {@code foo} producer side the
	 * contracts would look like this
	 * {@code src/test/resources/contracts/baz-service/some/contracts/...} and
	 * {@code src/test/resources/contracts/bar-service/some/contracts/...}.
	 *
	 * Then when the consumer with {@code spring.application.name} or the
	 * {@link AutoConfigureStubRunner#consumerName()} annotation parameter set to
	 * {@code baz-service} will define the test setup as follows
	 * {@code @AutoConfigureStubRunner(ids = "com.example:foo:+:stubs:8095", stubsPerConsumer=true)}
	 * then only the stubs registered under
	 * {@code src/test/resources/contracts/baz-service/some/contracts/...} will get
	 * registered and those under
	 * {@code src/test/resources/contracts/bar-service/some/contracts/...} will get
	 * ignored.
	 *
	 * @see <a href=
	 * "https://github.com/spring-cloud/spring-cloud-contract/issues/224">issue 224</a>
	 * @return {@code true} to turn on the feature
	 */
	boolean stubsPerConsumer() default false;

	/**
	 * You can override the default {@code spring.application.name} of this field by
	 * setting a value to this parameter.
	 *
	 * @see <a href=
	 * "https://github.com/spring-cloud/spring-cloud-contract/issues/224">issue 224</a>
	 * @return name of this application
	 */
	String consumerName() default "";

	/**
	 * For debugging purposes you can output the registered mappings to a given folder.
	 * Each HTTP server stub will have its own subfolder where all the mappings will get
	 * stored.
	 *
	 * @see <a href=
	 * "https://github.com/spring-cloud/spring-cloud-contract/issues/355">issue 355</a>
	 * @return where the mappings output should be stored
	 */
	String mappingsOutputFolder() default "";

	/**
	 * The way stubs should be found and registered. Defaults to
	 * {@link StubRunnerProperties.StubsMode#CLASSPATH}.
	 * @return the type of stubs mode
	 */
	StubRunnerProperties.StubsMode stubsMode() default StubRunnerProperties.StubsMode.CLASSPATH;

	/**
	 * Properties in form {@literal key=value}.
	 * @return the properties to add
	 */
	String[] properties() default {};

	/**
	 * @return If set to {@code false} will NOT delete stubs from a temporary folder after
	 * running tests
	 */
	boolean deleteStubsAfterTest() default true;

	/**
	 * @return When enabled, this flag will tell stub runner to not load the generated
	 * stubs, but convert the found contracts at runtime to a stub format and run those
	 * stubs.
	 */
	boolean generateStubs() default false;

	/**
	 * @return when enabled, this flag will tell stub runner to throw an exception when no
	 * stubs / contracts were found.
	 */
	boolean failOnNoStubs() default true;

	/**
	 * Configuration for an HTTP server stub.
	 * @return class that allows to perform additional HTTP server stub configuration
	 */
	Class<? extends HttpServerStubConfigurer> httpServerStubConfigurer() default NoOpHttpServerStubConfigurer.class;

}
