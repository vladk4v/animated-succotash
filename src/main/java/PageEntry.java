public class PageEntry implements Comparable<PageEntry> {
	private final String pdfName;
	private final int page;
	private final int count;

	public PageEntry(String pdfName, int page, int count) {
		this.pdfName = pdfName;
		this.page = page;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public int getPage() {
		return page;
	}

	public String getPdfName() {
		return pdfName;
	}

	@Override
	public String toString() {
		return "PageEntry{" +
				"pdfName='" + pdfName + '\'' +
				", page=" + page +
				", count=" + count +
				'}';
	}

	@Override
	public int compareTo(PageEntry o) {

		if (this.getCount() == o.getCount()) {
			if (this.getPdfName().equals(o.getPdfName())) {
				if (this.getPage() == o.getPage()) {
					return 0;
				} else if (this.getPage() < o.getPage()) {
					return -1;
				} else {
					return 1;
				}
			} else {
				return this.getPdfName().compareTo(o.getPdfName());
			}
		} else if (this.getCount() < o.getCount()) {
			return 1;
		} else {
			return -1;
		}
	}
}
