import java.util.LinkedList;
import java.util.List;

public class Page<T> {
    Integer pno, maxpno, maxrows, psize, rowno;
    List<T> list = new LinkedList<>();

    public Page(Integer maxrows) {
        this.maxrows = maxrows;
    }

    public Page() {
    }

    public boolean isFirstPage() {
        return pno == 1;
    }

    public boolean isLastPage() {
        return pno == maxpno;
    }

    public Integer getPno() {
        return pno;
    }

    public void setPno(Integer pno) {
        this.pno = pno;
    }

    public Integer getMaxpno() {
        return maxpno;
    }

    public void setMaxpno(Integer maxpno) {
        this.maxpno = maxpno;
    }

    public Integer getMaxrows() {
        return maxrows;
    }

    public void setMaxrows(Integer maxrows) {
        this.maxrows = maxrows;
    }

    public Integer getPsize() {
        return psize;
    }

    public void setPsize(Integer psize) {
        this.psize = psize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    /**
     * rowno从0开始计数
     *
     * @return
     */
    public Integer getRowno() {
        return rowno;
    }

    public void setRowno(Integer rowno) {
        this.rowno = rowno;
    }
}