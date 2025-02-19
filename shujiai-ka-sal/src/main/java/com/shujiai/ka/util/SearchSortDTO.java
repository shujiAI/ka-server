package com.shujiai.ka.util;


import java.io.Serializable;

public class SearchSortDTO implements Serializable {
    private String fieldName;
    private String order;

    public static SearchSortDTO.SearchSortDTOBuilder builder() {
        return new SearchSortDTO.SearchSortDTOBuilder();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getOrder() {
        return this.order;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public void setOrder(final String order) {
        this.order = order;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof SearchSortDTO)) {
            return false;
        } else {
            SearchSortDTO other = (SearchSortDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$fieldName = this.getFieldName();
                Object other$fieldName = other.getFieldName();
                if (this$fieldName == null) {
                    if (other$fieldName != null) {
                        return false;
                    }
                } else if (!this$fieldName.equals(other$fieldName)) {
                    return false;
                }

                Object this$order = this.getOrder();
                Object other$order = other.getOrder();
                if (this$order == null) {
                    if (other$order != null) {
                        return false;
                    }
                } else if (!this$order.equals(other$order)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SearchSortDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $fieldName = this.getFieldName();
        result = result * PRIME + ($fieldName == null ? 43 : $fieldName.hashCode());
        Object $order = this.getOrder();
        result = result * PRIME + ($order == null ? 43 : $order.hashCode());
        return result;
    }

    public String toString() {
        return "SearchSortDTO(fieldName=" + this.getFieldName() + ", order=" + this.getOrder() + ")";
    }

    public SearchSortDTO() {
    }

    public SearchSortDTO(final String fieldName, final String order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    public static class SearchSortDTOBuilder {
        private String fieldName;
        private String order;

        SearchSortDTOBuilder() {
        }

        public SearchSortDTO.SearchSortDTOBuilder fieldName(final String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public SearchSortDTO.SearchSortDTOBuilder order(final String order) {
            this.order = order;
            return this;
        }

        public SearchSortDTO build() {
            return new SearchSortDTO(this.fieldName, this.order);
        }

        public String toString() {
            return "SearchSortDTO.SearchSortDTOBuilder(fieldName=" + this.fieldName + ", order=" + this.order + ")";
        }
    }

    public static enum OrderEnum {
        ASC,
        DESC;

        private OrderEnum() {
        }
    }
}

