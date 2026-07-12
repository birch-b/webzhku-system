package com.taobao.dao;

import java.util.List;
import java.util.Map;

public interface AddressDAO {
    List<Map<String, Object>> listByUserId(Long userId);
    Map<String, Object> getByIdAndUserId(Long addrId, Long userId);
    void add(Long userId, String receiverName, String phone, String province,
             String city, String district, String detail, int isDefault);
    void update(Long addrId, Long userId, String receiverName, String phone,
                String province, String city, String district, String detail, int isDefault);
    void delete(Long addrId, Long userId);
    void clearDefault(Long userId);
    void setDefault(Long addrId, Long userId);
}