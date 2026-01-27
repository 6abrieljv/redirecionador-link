package com.redirecionador.redirecionador.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import java.net.InetAddress;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class GeoIpService {
    private static final String UNKNOWN = "Unknown";

    private final DatabaseReader databaseReader;

    public GeoIpService(ObjectProvider<DatabaseReader> databaseReaderProvider) {
        this.databaseReader = databaseReaderProvider.getIfAvailable();
    }

    public GeoIpDetails resolve(String ipAddress) {
        if (databaseReader == null || ipAddress == null || ipAddress.isBlank()) {
            return GeoIpDetails.unknown();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = databaseReader.city(inetAddress);
            return new GeoIpDetails(
                    valueOrUnknown(response.getCountry().getIsoCode()),
                    valueOrUnknown(response.getCountry().getName()),
                    valueOrUnknown(response.getMostSpecificSubdivision().getName()),
                    valueOrUnknown(response.getCity().getName())
            );
        } catch (Exception ex) {
            return GeoIpDetails.unknown();
        }
    }

    private String valueOrUnknown(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        return value;
    }

    public record GeoIpDetails(
            String countryIso,
            String countryName,
            String regionName,
            String cityName
    ) {
        public static GeoIpDetails unknown() {
            return new GeoIpDetails(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        }
    }
}
