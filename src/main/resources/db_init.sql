-- ============================================
-- 淘宝购物系统 数据库初始化脚本（统一版）
-- 适用于：4人小组分工协作开发
-- 使用方式：在MySQL中直接执行本脚本即可
-- 注意：本文件与项目根目录的db_init.sql保持一致
-- ============================================

-- 删除旧数据库（如需保留数据请注释掉下面两行）
DROP DATABASE IF EXISTS taobao_shop;
CREATE DATABASE taobao_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE taobao_shop;

-- ============================================
-- 1. 用户表（统一存放四类用户：浏览者/顾客/商家/运营商）
-- ============================================
CREATE TABLE `user` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    `password`      VARCHAR(128) NOT NULL COMMENT '密码（MD5加密）',
    `nickname`      VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `avatar`        VARCHAR(255) DEFAULT '/upload/avatar/default.png' COMMENT '头像',
    `role`          VARCHAR(20)  NOT NULL DEFAULT 'customer' COMMENT '角色：browser=浏览者, customer=顾客, shopkeeper=商家, operator=运营商',
    `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`         VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status`        TINYINT      DEFAULT 1 COMMENT '状态：1=正常, 0=封禁',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 商家入驻申请表
-- ============================================
CREATE TABLE `shop_apply` (
    `id`              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    `user_id`         BIGINT       NOT NULL COMMENT '申请人用户ID',
    `shop_name`       VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `shop_category`   VARCHAR(50)  DEFAULT NULL COMMENT '主营类目',
    `description`     TEXT         COMMENT '店铺描述/简介',
    `contact_name`    VARCHAR(50)  DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `contact_email`   VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    `id_card`         VARCHAR(20)  DEFAULT NULL COMMENT '身份证号',
    `license_no`      VARCHAR(50)  DEFAULT NULL COMMENT '营业执照号',
    `license_img`     VARCHAR(255) DEFAULT NULL COMMENT '营业执照图片',
    `status`          TINYINT      DEFAULT 0 COMMENT '状态：0=待审核, 1=已通过, 2=已拒绝',
    `reject_reason`   VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
    `apply_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `review_time`     DATETIME     DEFAULT NULL COMMENT '审核时间',
    `reviewer_id`     BIGINT       DEFAULT NULL COMMENT '审核人ID',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家入驻申请表';

-- ============================================
-- 3. 店铺表
-- ============================================
CREATE TABLE `shop` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '店铺ID',
    `user_id`       BIGINT       NOT NULL UNIQUE COMMENT '店主用户ID',
    `shop_name`     VARCHAR(100) NOT NULL COMMENT '店铺名称',
    `shop_category` VARCHAR(50)  DEFAULT NULL COMMENT '主营类目',
    `description`   TEXT         COMMENT '店铺简介',
    `avatar`        VARCHAR(255) DEFAULT '/upload/shop/default.png' COMMENT '店铺头像',
    `status`        TINYINT      DEFAULT 1 COMMENT '状态：1=营业中, 0=休息中, -1=关闭',
    `rating`        DECIMAL(2,1) DEFAULT 5.0 COMMENT '店铺评分',
    `total_orders`  INT          DEFAULT 0 COMMENT '总订单数',
    `total_sales`   DECIMAL(12,2) DEFAULT 0 COMMENT '总销售额',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- ============================================
-- 4. 商品分类表（店铺内部分类）
-- ============================================
CREATE TABLE `category` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `shop_id`     BIGINT      NOT NULL COMMENT '所属店铺ID',
    `parent_id`   BIGINT      DEFAULT 0 COMMENT '父分类ID，0表示顶级',
    `name`        VARCHAR(50) NOT NULL COMMENT '分类名称',
    `sort_order`  INT         DEFAULT 0 COMMENT '排序序号',
    `status`      TINYINT     DEFAULT 1 COMMENT '状态：1=启用, 0=禁用',
    `create_time` DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`shop_id`) REFERENCES `shop`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================
-- 5. 商品表
-- ============================================
CREATE TABLE `product` (
    `id`             BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    `shop_id`        BIGINT         NOT NULL COMMENT '所属店铺ID',
    `category_id`    BIGINT         DEFAULT NULL COMMENT '所属分类ID',
    `name`           VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `subtitle`       VARCHAR(200)   DEFAULT NULL COMMENT '副标题/卖点',
    `description`    TEXT           COMMENT '商品详情（富文本）',
    `price`          DECIMAL(10,2)  NOT NULL COMMENT '单价',
    `original_price` DECIMAL(10,2)  DEFAULT NULL COMMENT '原价',
    `stock`          INT            DEFAULT 0 COMMENT '库存数量',
    `sales`          INT            DEFAULT 0 COMMENT '销量',
    `status`         TINYINT        DEFAULT 0 COMMENT '状态：0=待上架, 1=上架中, 2=已下架, 3=已售罄',
    `images`         TEXT           COMMENT '多图，JSON数组存储，如["img1.jpg","img2.jpg"]',
    `cover_image`    VARCHAR(255)   DEFAULT NULL COMMENT '封面图',
    `weight`         DECIMAL(8,2)   DEFAULT NULL COMMENT '重量（kg）',
    `create_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `publish_time`   DATETIME       DEFAULT NULL COMMENT '上架时间',
    FOREIGN KEY (`shop_id`)     REFERENCES `shop`(`id`)     ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================
-- 6. 收货地址表
-- ============================================
CREATE TABLE `address` (
    `id`            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    `phone`         VARCHAR(20)  NOT NULL COMMENT '收货人电话',
    `province`      VARCHAR(50)  DEFAULT NULL COMMENT '省份',
    `city`          VARCHAR(50)  DEFAULT NULL COMMENT '城市',
    `district`      VARCHAR(50)  DEFAULT NULL COMMENT '区县',
    `detail`        VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default`    TINYINT      DEFAULT 0 COMMENT '是否默认：1=是, 0=否',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ============================================
-- 7. 购物车表
-- ============================================
CREATE TABLE `cart_item` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '购买数量',
    `selected`    TINYINT  DEFAULT 1 COMMENT '是否选中：1=选中, 0=未选',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`)    REFERENCES `user`(`id`)    ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================
-- 8. 订单表
-- ============================================
CREATE TABLE `order` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no`         VARCHAR(32)    NOT NULL UNIQUE COMMENT '订单编号',
    `user_id`          BIGINT         NOT NULL COMMENT '买家用户ID',
    `shop_id`          BIGINT         NOT NULL COMMENT '卖家店铺ID',
    `total_amount`     DECIMAL(12,2)  NOT NULL COMMENT '订单总金额',
    `discount_amount`  DECIMAL(12,2)  DEFAULT 0 COMMENT '优惠金额',
    `pay_amount`       DECIMAL(12,2)  NOT NULL COMMENT '实付金额',
    `pay_method`       TINYINT        DEFAULT NULL COMMENT '支付方式：1=微信, 2=支付宝, 3=银行卡',
    `status`           TINYINT        DEFAULT 0 COMMENT '状态：0=待付款, 1=待发货, 2=已发货, 3=已收货, 4=已完成, 5=已取消, 6=退款中, 7=已退款',
    `receiver_name`    VARCHAR(50)    DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone`   VARCHAR(20)    DEFAULT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(255)   DEFAULT NULL COMMENT '收货地址',
    `buyer_message`    VARCHAR(255)   DEFAULT NULL COMMENT '买家留言',
    `create_time`      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `pay_time`         DATETIME       DEFAULT NULL COMMENT '支付时间',
    `ship_time`        DATETIME       DEFAULT NULL COMMENT '发货时间',
    `receive_time`     DATETIME       DEFAULT NULL COMMENT '收货时间',
    `finish_time`      DATETIME       DEFAULT NULL COMMENT '完成时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`shop_id`) REFERENCES `shop`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================
-- 9. 订单商品明细表
-- ============================================
CREATE TABLE `order_item` (
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    `order_id`     BIGINT         NOT NULL COMMENT '订单ID',
    `product_id`   BIGINT         NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200)   NOT NULL COMMENT '商品名称（快照）',
    `cover_image`  VARCHAR(255)   DEFAULT NULL COMMENT '商品封面（快照）',
    `price`        DECIMAL(10,2)  NOT NULL COMMENT '单价（快照）',
    `quantity`     INT            NOT NULL COMMENT '购买数量',
    `subtotal`     DECIMAL(12,2)  NOT NULL COMMENT '小计金额',
    FOREIGN KEY (`order_id`)   REFERENCES `order`(`id`)   ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';

-- ============================================
-- 10. 物流表
-- ============================================
CREATE TABLE `logistics` (
    `id`               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '物流ID',
    `order_id`         BIGINT       NOT NULL UNIQUE COMMENT '订单ID',
    `company`          VARCHAR(50)  DEFAULT NULL COMMENT '快递公司',
    `tracking_no`      VARCHAR(50)  DEFAULT NULL COMMENT '快递单号',
    `receiver_name`    VARCHAR(50)  DEFAULT NULL COMMENT '收件人',
    `receiver_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '收件电话',
    `receiver_address` VARCHAR(255) DEFAULT NULL COMMENT '收件地址',
    `status`           TINYINT      DEFAULT 0 COMMENT '状态：0=未发货, 1=运输中, 2=派送中, 3=已签收',
    `ship_time`        DATETIME     DEFAULT NULL COMMENT '发货时间',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`order_id`) REFERENCES `order`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流表';

-- ============================================
-- 11. 商品评价表
-- ============================================
CREATE TABLE `review` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    `order_id`    BIGINT       NOT NULL COMMENT '订单ID',
    `product_id`  BIGINT       NOT NULL COMMENT '商品ID',
    `user_id`     BIGINT       NOT NULL COMMENT '买家用户ID',
    `shop_id`     BIGINT       NOT NULL COMMENT '卖家店铺ID',
    `rating`      TINYINT      NOT NULL COMMENT '评分 1-5',
    `content`     TEXT         COMMENT '评价内容',
    `images`      TEXT         COMMENT '评价图片，JSON数组',
    `reply`       TEXT         COMMENT '商家回复',
    `reply_time`  DATETIME     DEFAULT NULL COMMENT '商家回复时间',
    `status`      TINYINT      DEFAULT 1 COMMENT '状态：0=隐藏, 1=显示',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`order_id`)   REFERENCES `order`(`id`),
    FOREIGN KEY (`product_id`) REFERENCES `product`(`id`),
    FOREIGN KEY (`user_id`)    REFERENCES `user`(`id`),
    FOREIGN KEY (`shop_id`)    REFERENCES `shop`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- ============================================
-- 12. 平台公告表
-- ============================================
CREATE TABLE `announcement` (
    `id`           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    `title`        VARCHAR(200) NOT NULL COMMENT '公告标题',
    `content`      TEXT         COMMENT '公告内容(HTML富文本)',
    `operator_id`  BIGINT       DEFAULT NULL COMMENT '发布人(运营商)ID',
    `priority`     INT          DEFAULT 0 COMMENT '优先级：0=普通, 1=重要',
    `status`       TINYINT      DEFAULT 1 COMMENT '状态：0=草稿, 1=已发布, 2=已归档',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_at` DATETIME     DEFAULT NULL COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台公告表';

-- ============================================
-- 13. 售后/退款表（可选，预留扩展）
-- ============================================
CREATE TABLE `aftersale` (
    `id`          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '售后ID',
    `order_id`    BIGINT         NOT NULL COMMENT '订单ID',
    `user_id`     BIGINT         NOT NULL COMMENT '申请人用户ID',
    `type`        TINYINT        NOT NULL COMMENT '类型：1=仅退款, 2=退货退款',
    `reason`      VARCHAR(255)   NOT NULL COMMENT '申请原因',
    `amount`      DECIMAL(12,2)  NOT NULL COMMENT '退款金额',
    `description` TEXT           COMMENT '详细说明',
    `images`      TEXT           COMMENT '凭证图片JSON数组',
    `status`      TINYINT        DEFAULT 0 COMMENT '状态：0=待处理, 1=同意, 2=拒绝, 3=已退款',
    `shop_reply`  VARCHAR(255)   DEFAULT NULL COMMENT '商家处理备注',
    `create_time` DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `handle_time` DATETIME       DEFAULT NULL COMMENT '处理时间',
    FOREIGN KEY (`order_id`) REFERENCES `order`(`id`),
    FOREIGN KEY (`user_id`)  REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后退款表';

-- ============================================
-- 索引优化
-- ============================================
CREATE INDEX idx_user_role      ON `user`(`role`);
CREATE INDEX idx_user_status    ON `user`(`status`);
CREATE INDEX idx_shop_user      ON `shop`(`user_id`);
CREATE INDEX idx_shop_status    ON `shop`(`status`);
CREATE INDEX idx_apply_user     ON `shop_apply`(`user_id`);
CREATE INDEX idx_apply_status   ON `shop_apply`(`status`);
CREATE INDEX idx_category_shop  ON `category`(`shop_id`);
CREATE INDEX idx_product_shop   ON `product`(`shop_id`);
CREATE INDEX idx_product_status ON `product`(`status`);
CREATE INDEX idx_product_cat    ON `product`(`category_id`);
CREATE INDEX idx_address_user   ON `address`(`user_id`);
CREATE INDEX idx_cart_user      ON `cart_item`(`user_id`);
CREATE INDEX idx_order_user     ON `order`(`user_id`);
CREATE INDEX idx_order_shop     ON `order`(`shop_id`);
CREATE INDEX idx_order_status   ON `order`(`status`);
CREATE INDEX idx_order_no       ON `order`(`order_no`);
CREATE INDEX idx_order_item_order ON `order_item`(`order_id`);
CREATE INDEX idx_review_shop    ON `review`(`shop_id`);
CREATE INDEX idx_review_product ON `review`(`product_id`);
CREATE INDEX idx_ann_status     ON `announcement`(`status`);

-- ============================================
-- 初始测试数据（4人分工演示用）
-- ============================================

-- ---------- 用户数据 ----------
-- 运营商（组员1演示用） 密码：admin123
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `phone`, `email`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '平台运营商', 'operator', '13800000000', 'admin@taobao.com', 1);

-- 商家用户（组员2演示用） 密码：123456
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `phone`, `email`, `status`) VALUES
('shop001', 'e10adc3949ba59abbe56e057f20f883e', '数码小铺店主', 'shopkeeper', '13800138001', 'shop001@test.com', 1),
('shop002', 'e10adc3949ba59abbe56e057f20f883e', '服饰精品店店主', 'shopkeeper', '13800138002', 'shop002@test.com', 1);

-- 顾客用户（组员3演示用） 密码：123456
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `phone`, `email`, `status`) VALUES
('buyer001', 'e10adc3949ba59abbe56e057f20f883e', '小明同学', 'customer', '13900139001', 'buyer001@test.com', 1),
('buyer002', 'e10adc3949ba59abbe56e057f20f883e', '购物达人', 'customer', '13900139002', 'buyer002@test.com', 1);

-- 浏览者（未注册，浏览者权限由Filter控制，不需要单独数据）

-- ---------- 店铺数据（组员2演示用） ----------
INSERT INTO `shop` (`user_id`, `shop_name`, `shop_category`, `description`, `status`, `rating`) VALUES
(2, '数码小铺', '数码电子', '专注数码产品销售，品质保证，售后无忧。', 1, 4.8),
(3, '服饰精品店', '服装鞋包', '潮流服饰，时尚穿搭，你想要的都在这里。', 1, 4.6);

-- ---------- 商品分类数据 ----------
INSERT INTO `category` (`shop_id`, `parent_id`, `name`, `sort_order`, `status`) VALUES
(1, 0, '手机数码', 1, 1),
(1, 0, '电脑办公', 2, 1),
(1, 1, '智能手机', 1, 1),
(1, 1, '平板电脑', 2, 1),
(2, 0, '男装', 1, 1),
(2, 0, '女装', 2, 1);

-- ---------- 商品数据 ----------
INSERT INTO `product` (`shop_id`, `category_id`, `name`, `subtitle`, `description`, `price`, `original_price`, `stock`, `sales`, `status`, `cover_image`, `publish_time`) VALUES
(1, 3, '旗舰智能手机 Pro Max', '新品上市 限时特惠', '搭载最新处理器，超清屏幕，超长续航。', 5999.00, 6999.00, 100, 25, 1, 'https://picsum.photos/seed/1/300/300', NOW()),
(1, 3, '性价比手机', '千元机首选', '大屏幕大电池，日常使用无压力。', 1299.00, 1599.00, 200, 56, 1, 'https://picsum.photos/seed/2/300/300', NOW()),
(1, 4, '轻薄平板电脑', '办公娱乐两不误', '10.9英寸屏幕，支持触控笔，轻薄便携。', 3299.00, 3699.00, 80, 12, 1, 'https://picsum.photos/seed/3/300/300', NOW()),
(2, 5, '男士休闲T恤', '夏季新品', '纯棉面料，舒适透气，多色可选。', 99.00, 159.00, 500, 88, 1, 'https://picsum.photos/seed/4/300/300', NOW()),
(2, 6, '女士连衣裙', '气质优雅', '修身显瘦，时尚百搭，约会必备。', 259.00, 399.00, 150, 43, 1, 'https://picsum.photos/seed/5/300/300', NOW());

-- ---------- 收货地址数据（组员3演示用） ----------
INSERT INTO `address` (`user_id`, `receiver_name`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(4, '小明', '13900139001', '浙江省', '杭州市', '西湖区', '文三路123号XX小区1栋101室', 1),
(4, '小明同学', '13900139001', '上海市', '上海市', '浦东新区', '张江高科技园区456号', 0),
(5, '购物达人', '13900139002', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城', 1);

-- ---------- 购物车数据（组员3演示用） ----------
INSERT INTO `cart_item` (`user_id`, `product_id`, `quantity`, `selected`) VALUES
(4, 1, 1, 1),
(4, 4, 2, 1),
(4, 5, 1, 0);

-- ---------- 平台公告数据（组员1演示用） ----------
INSERT INTO `announcement` (`title`, `content`, `operator_id`, `priority`, `status`, `published_at`) VALUES
('欢迎来到淘宝购物系统', '<p>欢迎使用淘宝购物系统！本系统为课程设计项目，包含完整的购物流程。</p>', 1, 1, 1, NOW()),
('系统上线通知', '<p>系统正式上线运营，如有问题请联系客服。</p>', 1, 0, 1, NOW());

-- ---------- 商家入驻申请数据（组员1/2演示用） ----------
-- 已通过的申请（上面的店铺已创建）
INSERT INTO `shop_apply` (`user_id`, `shop_name`, `shop_category`, `description`, `contact_name`, `contact_phone`, `id_card`, `license_no`, `status`, `apply_time`, `review_time`, `reviewer_id`) VALUES
(2, '数码小铺', '数码电子', '专注数码产品销售', '张老板', '13800138001', '330100199001011234', '913300001234567890', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 1),
(3, '服饰精品店', '服装鞋包', '潮流服饰销售', '李老板', '13800138002', '330100199202022345', '913300002345678901', 1, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 1);

-- 待审核的申请（用于组员1审核演示）
INSERT INTO `shop_apply` (`user_id`, `shop_name`, `shop_category`, `description`, `contact_name`, `contact_phone`, `id_card`, `license_no`, `status`, `apply_time`) VALUES
(4, '小明的零食店', '食品零食', '各种美味零食', '小明', '13900139001', '330100199505053456', '913300003456789012', 0, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ---------- 示例订单数据 ----------
-- 一个已完成的订单
INSERT INTO `order` (`order_no`, `user_id`, `shop_id`, `total_amount`, `discount_amount`, `pay_amount`, `pay_method`, `status`, `receiver_name`, `receiver_phone`, `receiver_address`, `create_time`, `pay_time`, `ship_time`, `receive_time`, `finish_time`) VALUES
('2025062000001', 4, 1, 5999.00, 0.00, 5999.00, 2, 4, '小明', '13900139001', '浙江省杭州市西湖区文三路123号', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `cover_image`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, '旗舰智能手机 Pro Max', '/upload/product/phone1.jpg', 5999.00, 1, 5999.00);

INSERT INTO `logistics` (`order_id`, `company`, `tracking_no`, `receiver_name`, `receiver_phone`, `receiver_address`, `status`, `ship_time`) VALUES
(1, '顺丰速运', 'SF1234567890', '小明', '13900139001', '浙江省杭州市西湖区文三路123号', 3, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 一个已评价的订单
INSERT INTO `review` (`order_id`, `product_id`, `user_id`, `shop_id`, `rating`, `content`, `status`, `create_time`) VALUES
(1, 1, 4, 1, 5, '手机非常好用，物流很快，好评！', 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ============================================
-- 脚本执行完成
-- ============================================
