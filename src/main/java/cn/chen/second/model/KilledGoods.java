package cn.chen.second.model;

public class KilledGoods {
    private int goodsId;
    private String phone;
    private String createTime;
    public KilledGoods() {}
    public KilledGoods(int goodsId, String phone) {
        this.goodsId = goodsId;
        this.phone = phone;
    }


    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
