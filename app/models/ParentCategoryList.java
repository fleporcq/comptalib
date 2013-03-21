package models;


import java.util.ArrayList;
import java.util.List;

public class ParentCategoryList extends ArrayList<Category> {



    public ParentCategoryList() {

    }

    public ParentCategoryList(List<Category> categories) {
        this.addAll(categories);
    }

    public List<Category> getLeafs() {

        List<Category> leafs = new ArrayList<Category>();

        for (Category category : this) {
            if (!category.hasChildren()) {
                leafs.add(category);
            } else {
                leafs.addAll(category.children);
            }
        }

        return leafs;
    }

    public int countLeafs() {
        return getLeafs().size();
    }

    public List<Category> getChildren() {

        List<Category> children = new ArrayList<Category>();

        for (Category category : this) {
            children.addAll(category.children);
        }

        return children;
    }

    public List<ParentCategoryList> paginate(int colsPerPage, int colsFirstPage){

        if(colsFirstPage == 0){
            colsFirstPage = colsPerPage;
        }

        List<ParentCategoryList> pages = new ArrayList<ParentCategoryList>();
        ParentCategoryList page = new ParentCategoryList();
        boolean firstPage = true;
        for (Category category : this) {
            int leafCount = category.hasChildren() ? category.children.size() : 1;
            if(page.countLeafs() + leafCount  > (firstPage ? colsFirstPage : colsPerPage)){
                pages.add(page);
                page = new ParentCategoryList();
                firstPage = false;
            }
            page.add(category);
        }
        pages.add(page);

        return pages;
    }

}
