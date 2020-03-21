package org.cloud.provider.entity;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Supplier implements java.io.Serializable {

    public Supplier(int type) {
        this.type = getType(type);
    }

    /**
     * 供应山名称
     */
    private Supplier.Type type;

    public enum Type {
        DIDI("滴滴", 0),//滴滴
        DIDA("滴答", 1),//滴答
        CAOCAO("曹操", 2);//曹操

        Type(String name, int id) {
            _name = name;
            _id = id;
        }

        private String _name;
        private int _id;

        public String getName() {
            return _name;
        }

        public int getId() {
            return _id;
        }
    }

    public static Supplier.Type getType(int num) {
        switch (num) {
            case 0:
                return Supplier.Type.DIDI;
            case 1:
                return Supplier.Type.DIDA;
            default:
                return Supplier.Type.CAOCAO;
        }
    }
}
