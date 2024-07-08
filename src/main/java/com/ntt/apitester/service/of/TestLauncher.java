package com.ntt.apitester.service.of;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TestLauncher implements ApplicationRunner {

    @Autowired
    private EnrichmentTest enrichmentTest;
    @Autowired
    private RelatedNetworkTest relatedNetworkTest;

    @Autowired
    private  GeoCodeV2Test geoCodeV2Test;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //enrichmentTest.test();
        //relatedNetworkTest.test();
        geoCodeV2Test.test();
    }
}
