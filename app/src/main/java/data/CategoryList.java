package data;

/**
 * Created by kushal on 5/4/15.
 */
public class CategoryList {
    String CategoryId, CategoryName;

    public CategoryList(String categoryId, String categoryName) {

        CategoryId = categoryId;
        CategoryName = categoryName;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
