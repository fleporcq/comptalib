package models;


import java.util.ArrayList;
import java.util.List;

public class ParentCategoryList extends ArrayList<Category> {

    List<Category> leafs;

    List<Category> children;


    public ParentCategoryList(List<Category> categories) {
        this.addAll(categories);
    }

    public List<Category> getLeafs() {
        if (leafs == null) {
            leafs = new ArrayList<Category>();
            for (Category category : this) {
                if (!category.hasChildren()) {
                    leafs.add(category);
                } else {
                    leafs.addAll(category.children);
                }
            }
        }
        return leafs;
    }

    public List<Category> getChildren() {
        if (children == null) {
            children = new ArrayList<Category>();
            for (Category category : this) {
                children.addAll(category.children);
            }
        }
        return children;
    }

}
