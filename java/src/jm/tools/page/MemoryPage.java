package jm.tools.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ��List�����ڴ��ҳ,Ĭ��ҳ��С��10
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
	 * ����ָ��ҳ�������
	 * @param pageNum ָ��ҳ��
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
	 * ���ص�ǰҳ�������
	 * @return
	 */
	public List getPageResult(){
		return Collections.unmodifiableList(this.getPageResult(currPageNum));
	}

	/**
	 * ���ص�ǰҳ��
	 * @return
	 */
	public int getCurrPageNum() {
		return currPageNum;
	}

	/**
	 * ���õ�ǰҳ��
	 * @param currPageNum
	 */
	public void setCurrPageNum(int currPageNum) {
		this.currPageNum = checkPageNumValidate(currPageNum);
	}

	/**
	 * ����ҳ��С
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * ����ҳ��С,ҳ�����Զ����¼���
	 * @param pageSize ҳ��С
	 */
	public void setPageSize(int pageSize) {
		if(pageSize <= 0){
			throw new RuntimeException("ҳ��С�������0");
		}
		this.pageSize = pageSize;
		calcPageCount();
	}

	/**
	 * ����ҳ��
	 * @return
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * ��������Դ,ҳ�����Զ����¼���
	 * @param dataList ����Դ
	 */
	public void setDataList(List dataList) {
		if(dataList == null){
			throw new NullPointerException("����Դ����Ϊ��");
		}
		this.dataList = dataList;
		calcPageCount();
	}
	
	/**
	 * ����ҳ��
	 */
	private void calcPageCount(){
		pageCount = (dataList.size() + pageSize - 1) / pageSize;
	}
	
	/**
	 * ��ҳ�������
	 * @param pageNum ҳ��
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
	 * ��ҳ�յ�����
	 * @param pageNum ҳ��
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
