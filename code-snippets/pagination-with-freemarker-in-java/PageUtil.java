import java.util.List;

public class PageUtil {
    public static Page make(int maxrows, List<Page> pl, int rowoffset) {
        Page p = null;
        return p;
    }

    /**
     * offset & rows/page & total rows ==>> page no & etc.
     *
     * @param page
     */
    public static void arrange(Page page) {
        int pno = (page.getRowno()) / page.getPsize() + 1; // rowno 从0开始数…
        int maxpno = (page.getMaxrows() - 1) / page.getPsize() + 1;
        page.setPno(pno);
        page.setMaxpno(maxpno);
    }

    public static void main(String[] args) {
        Page page = new Page(50);
        page.setPsize(5);
        page.setRowno(10);
        arrange(page);
    }
}