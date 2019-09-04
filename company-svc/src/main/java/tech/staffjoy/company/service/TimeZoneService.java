package tech.staffjoy.company.service;

import java.util.TimeZone;

import org.springframework.stereotype.Service;

import tech.staffjoy.company.dto.TimeZoneList;

@Service
public class TimeZoneService {

    public TimeZoneList listTimeZones() {
        TimeZoneList timeZoneList = TimeZoneList.builder().build();
        for(String id : TimeZone.getAvailableIDs()) {
            timeZoneList.getTimezones().add(id);
        }
        return timeZoneList;
    }
}
