package com.taobao.service;

import java.util.List;
import java.util.Map;

public interface AddressService {
    // 根据用户ID查询全部收货地址
    List<Map<String, Object>> listAddressByUserId(Long userId);

    // 根据地址ID+用户ID获取单条地址（编辑回显）
    Map<String, Object> getAddressByIdAndUserId(Long addrId, Long userId);

    // 新增地址，自动处理默认地址互斥
    void addAddress(Long userId, String receiverName, String phone, String province,
                     String city, String district, String detail, int isDefault);

    // 修改地址，自动处理默认地址互斥
    void updateAddress(Long addrId, Long userId, String receiverName, String phone,
                       String province, String city, String district, String detail, int isDefault);

    // 删除地址
    void deleteAddress(Long addrId, Long userId);

    // 将指定地址设为默认，清除该用户其他默认
    void setDefaultAddress(Long addrId, Long userId);
}