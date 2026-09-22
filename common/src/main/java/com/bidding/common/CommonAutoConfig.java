package com.bidding.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
		basePackages = "com.bidding.common",
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.bidding\\.common\\.feign\\..*"))
public class CommonAutoConfig {
}
