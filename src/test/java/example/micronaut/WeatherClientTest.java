/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.micronaut;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import example.openmeteo.api.WeatherForecastApisApi;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

// tag::test[]
@MicronautTest
class WeatherClientTest {

    WireMockServer wireMockServer;

    @BeforeEach
    void init() {
        int wireMockPort = 7001;
        wireMockServer = new WireMockServer(wireMockPort);
        wireMockServer.start();
    }

    @AfterEach
    void end() {
        wireMockServer.stop();
    }
    @Test
    @DisplayName("Fetches weather for Montaigu-Vendée")
    void fetchesWeather(WeatherForecastApisApi api) {
        wireMockServer
                .stubFor(
                        WireMock.get(
                                        "/v1/forecast?latitude=46.97386&longitude=-1.3111076&current_weather=true&temperature_unit=celsius&windspeed_unit=kmh&timeformat=iso8601")
                                .willReturn(
                                        WireMock.aResponse()
                                                .withHeader("Content-Type", "application/json")
                                                .withBodyFile("responses/openmeteo.json")
                                )
                );// <1>
        var forecast = api.v1ForecastGet(46.97386f, -1.3111076f,    // <2>
                null,
                null,
                true,
                null,
                null,
                null,
                null,
                null);
        float _default = Objects.requireNonNull(forecast.block()).getDefault();
        assertTrue(_default < 3213211);
    }
}
// end::test[]
