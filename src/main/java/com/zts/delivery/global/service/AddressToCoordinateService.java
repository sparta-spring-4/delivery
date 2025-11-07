package com.zts.delivery.global.service;

import java.util.List;

public interface AddressToCoordinateService {
    List<Double> getCoordinate(String address);
}
