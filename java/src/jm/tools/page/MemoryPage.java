package jm.tools.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对List进行内存分页,默认页大小是10
 * @author yjm
 *
 */
@SuppressWarnings("unchecked")
public final class MemoryPage {
	private List dataList = new ArrayList(0);
	private int currPageNum = 1;
	private int pageSize = 10;
	private int pageCount;
	
	public MemoryPage(List dataList){
		this.setDataList(dataList);
	}
	
	public MemoryPage(){}
	
	/**
	 * 返回指定页码的数据
	 * @param pageNum 指定页码
	 * @return
	 */
	public List getPageResult(int pageNum){
		pageNum = checkPageNumValidate(pageNum);
		this.setCurrPageNum(pageNum);
		if(pageCount == 0){
			return new ArrayList(0);
		}
		return Collections.unmodifiableList(dataList.subList(this.getFromIndex(pageNum), this.getToIndex(pageNum)));
	}
	
	/**
	 * 返回当前页码的数据
	 * @return
	 */
	public List getPageResult(){
		return Collections.unmodifiableList(this.getPageResult(currPageNum));
	}

	/**
	 * 返回当前页码
	 * @return
	 */
	public int getCurrPageNum() {
		return currPageNum;
	}

	/**
	 * 设置当前页码
	 * @param currPageNum
	 */
	public void setCurrPageNum(int currPageNum) {
		this.currPageNum = checkPageNumValidate(currPageNum);
	}

	/**
	 * 返回页大小
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置页大小,页数会自动重新计算
	 * @param pageSize 页大小
	 */
	public void setPageSize(int pageSize) {
		if(pageSize <= 0){
			throw new RuntimeException("页大小必须大于0");
		}
		this.pageSize = pageSize;
		calcPageCount();
	}

	/**
	 * 返回页数
	 * @return
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * 设置数据源,页数会自动重新计算
	 * @param dataList 数据源
	 */
	public void setDataList(List dataList) {
		if(dataList == null){
			throw new NullPointerException("数据源不能为空");
		}
		this.dataList = dataList;
		calcPageCount();
	}
	
	/**
	 * 计算页数
	 */
	private void calcPageCount(){
		pageCount = (dataList.size() + pageSize - 1) / pageSize;
	}
	
	/**
	 * 分页起点索引
	 * @param pageNum 页码
	 * @return
	 */
	private int getFromIndex(int pageNum){
		int fromIndex = 0;
		if((pageNum-1)*pageSize < this.dataList.size()){
			fromIndex = (pageNum-1)*pageSize;
		}
		return fromIndex;
	}
	
	/**
	 * 分页终点索引
	 * @param pageNum 页码
	 * @return
	 */
	private int getToIndex(int pageNum){
		//int fromIndex = this.getFromIndex(pageNum);
		int toIndex = this.dataList.size();
		if(pageNum*pageSize < this.dataList.size()){
			toIndex = pageNum*pageSize;
		}
		return toIndex;
	}
	
	private int checkPageNumValidate(int pageNum) {
		if(pageNum < 1){
			pageNum = 1;
		}
		if(pageNum > pageCount && pageCount >0){
			pageNum = pageCount;
		}
		return pageNum;
	}
	
	public static void main(String[] args) {
		List dataList = new ArrayList();
		for(int i=0; i<21; ++i){
			dataList.add(i+"_"+i);
		}
		
		//MemoryPage page = new MemoryPage(dataList);
		MemoryPage page = new MemoryPage();
		page.setDataList(dataList);
		System.out.println(page.getCurrPageNum());
		System.out.println(page.getPageSize());
		System.out.println(page.getPageCount());
		page.setPageSize(5);
		page.setCurrPageNum(4);
		System.out.println(page.getPageResult());
		System.out.println(page.getCurrPageNum());
		System.out.println(page.getPageCount());
	}
	
}
