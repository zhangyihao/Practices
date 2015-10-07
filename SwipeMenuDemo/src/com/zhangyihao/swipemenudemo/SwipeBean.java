package com.zhangyihao.swipemenudemo;

public class SwipeBean {
	private String title;
	private boolean canDelete;
	private boolean canUpload;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isCanDelete() {
		return canDelete;
	}
	/**
	 * @param canDelete the canDelete to set
	 */
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	/**
	 * @return the canUpload
	 */
	public boolean isCanUpload() {
		return canUpload;
	}
	/**
	 * @param canUpload the canUpload to set
	 */
	public void setCanUpload(boolean canUpload) {
		this.canUpload = canUpload;
	}
	
	
}
