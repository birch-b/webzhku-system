package com.taobao.service.impl;

import com.taobao.dao.AddressDAO;
import com.taobao.dao.impl.AddressDAOImpl;
import com.taobao.service.AddressService;

import java.util.List;
import java.util.Map;

public class AddressServiceImpl implements AddressService {
    private AddressDAO addressDAO = new AddressDAOImpl();

    @Override
    public List<Map<String, Object>> listAddressByUserId(Long userId) {
        return addressDAO.listByUserId(userId);
    }

    @Override
    public Map<String, Object> getAddressByIdAndUserId(Long addrId, Long userId) {
        return addressDAO.getByIdAndUserId(addrId, userId);
    }

    @Override
    public void addAddress(Long userId, String receiverName, String phone, String province,
                           String city, String district, String detail, int isDefault) {
        addressDAO.add(userId, receiverName, phone, province, city, district, detail, isDefault);
    }

    @Override
    public void updateAddress(Long addrId, Long userId, String receiverName, String phone,
                              String province, String city, String district, String detail, int isDefault) {
        addressDAO.update(addrId, userId, receiverName, phone, province, city, district, detail, isDefault);
    }

    @Override
    public void deleteAddress(Long addrId, Long userId) {
        addressDAO.delete(addrId, userId);
    }

    @Override
    public void setDefaultAddress(Long addrId, Long userId) {
        addressDAO.setDefault(addrId, userId);
    }
}