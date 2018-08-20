package com.thewhite.blank.config;

import com.whitesoft.core.config.CoreMappersConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created on 20.08.2018
 *
 * @author Maxim Seredkin
 */
@Configuration
@Import(CoreMappersConfig.class)
public class MapperConfig {
}
