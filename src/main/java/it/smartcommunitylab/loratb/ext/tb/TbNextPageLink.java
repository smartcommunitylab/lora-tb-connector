package it.smartcommunitylab.loratb.ext.tb;

public class TbNextPageLink {
	private String idOffset;
	private int limit;
	private String textOffset;
	private String textSearch;
	private String textSearchBound;
	
	public String getIdOffset() {
		return idOffset;
	}
	public void setIdOffset(String idOffset) {
		this.idOffset = idOffset;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public String getTextOffset() {
		return textOffset;
	}
	public void setTextOffset(String textOffset) {
		this.textOffset = textOffset;
	}
	public String getTextSearch() {
		return textSearch;
	}
	public void setTextSearch(String textSearch) {
		this.textSearch = textSearch;
	}
	public String getTextSearchBound() {
		return textSearchBound;
	}
	public void setTextSearchBound(String textSearchBound) {
		this.textSearchBound = textSearchBound;
	}
}
