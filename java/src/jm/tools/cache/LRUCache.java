package jm.tools.cache;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * LRUCache
 * @author yjm
 *
 */
@SuppressWarnings("unchecked")
public class LRUCache {
	private Map cache = new Hashtable(); 
	private LinkedList lruList = new LinkedList();
	private int currentSize = 0;
	private int maxSize;
	// ����
	private long lifetime;

	/**
	 * ����һ������
	 * @param size �����С
	 * @param lifetime �����ж������������,С�ڵ���0��ʾ��Զ������
	 */
	public LRUCache(int size, long lifetime) {
		if(size <= 0){
			throw new AssertionError("�����С�������0");
		}
		this.maxSize = size;
		this.lifetime = lifetime;
	}
	
	/**
	 * ��ջ���
	 */
	public void clear() {
		this.cache.clear();
		this.lruList.clear();
	}

	/**
	 * �ӻ�����ȡֵ
	 * @param key �ؼ���
	 * @return �����ֵ
	 */
	public Object get(Object key) {
		if (this.currentSize > this.maxSize) {
			throw new AssertionError("cache state corrupted");
		}
		CacheEntry e = (CacheEntry) this.cache.get(key);
		if (e == null)
			return null;
		if (e.isExpired()) {
			this.cache.remove(key);
			this.lruList.remove(key);
			this.currentSize--;
			return null;
		}
		updateUsed(key);
		return ((CacheEntry) this.cache.get(key)).getValue();
	}

	/**
	 * ������ֵ
	 * @param key �ؼ���
	 * @param value ֵ
	 * @return ��ֵ
	 */
	public Object put(Object key, Object value) {
		CacheEntry oldEntry = (CacheEntry) this.cache.get(key);
		if (oldEntry != null) {
			this.currentSize--;
		}
		
		CacheEntry e = new CacheEntry(value);
		this.cache.put(key, e);
		updateUsed(key);
		this.currentSize++;
		
		if (this.currentSize > this.maxSize) {
			removeExpired();
			while (this.currentSize > this.maxSize) {
				Object obj = this.lruList.removeLast();
				this.cache.remove(obj);
				this.currentSize--;
			}
		}
		if ((oldEntry == null) || (oldEntry.isExpired())) {
			return null;
		}
		return oldEntry.getValue();
	}
	
	/**
	 * �����Ƿ����ָ���ؼ���
	 * @param key �ؼ���
	 * @return
	 */
	public boolean contains(Object key){
		this.removeExpired();
		return this.cache.containsKey(key) && this.lruList.contains(key);
	}

	/**
	 * �ӻ�����ȥ��ָ���ؼ���
	 * @param key �ؼ���
	 * @return �ؼ��ֶ�Ӧ�Ķ���
	 */
	public Object remove(Object key) {
		this.lruList.remove(key);
		CacheEntry oldEntry = (CacheEntry) this.cache.remove(key);
		if (oldEntry != null) {
			this.currentSize--;
		}
		if ((oldEntry == null) || (oldEntry.isExpired())) {
			return null;
		}
		return oldEntry.getValue();
	}

	/**
	 * �鿴��������
	 * @return
	 */
	public String lookup(){
		this.removeExpired();
		int size = this.lruList.size();
		StringBuilder info = new StringBuilder(100);
		info.append("[");
		for(int i = 0; i < size; ++i){
			Object key = this.lruList.get(i);
			CacheEntry e = (CacheEntry)this.cache.get(key);
			info.append(key).append("=").append(e.getValue());
			if(i < size -1){
				info.append(",");
			}
		}
		info.append("]");
		return info.toString();
	}

	/**
	 * ���ػ��浱ǰ��С
	 * @return
	 */
	public int getCurrentSize() {
		return this.currentSize;
	}

	/**
	 * ���»���
	 * @param key
	 */
	private void updateUsed(Object key) {
		this.lruList.remove(key);
		this.lruList.addFirst(key);
	}

	/**
	 * ɾ�������й��ڵĶ���
	 */
	private void removeExpired() {
		if (this.lifetime >= 0) {
			Iterator i = this.cache.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry ent = (Map.Entry) i.next();
				CacheEntry testEntry = (CacheEntry) ent.getValue();
				if (testEntry.isExpired()) {
					i.remove();
					this.lruList.remove(ent.getKey());
					this.currentSize--;
				}
			}
		}
	}

	private class CacheEntry {
		private long expiration;
		private Object value;
		

		public CacheEntry(Object value) {
			this.value = value;
			
			if (LRUCache.this.lifetime > 0)
				this.expiration = (new Date().getTime() + LRUCache.this.lifetime);
		}

		public Object getValue() {
			return this.value;
		}

		public boolean isExpired() {
			if (LRUCache.this.lifetime <= 0) {
				return false;
			}
			return this.expiration < new Date().getTime();
		}
	}
	
	public static void main(String[] args) throws Exception {
		LRUCache m = new LRUCache(2, 0);
		
		
		String value = (String)m.put("name", "fdconan");
		System.out.println("cache name=fdconan, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		value = (String)m.put("name", "fdconan_new");
		System.out.println("cache name=fdconan_new, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		value = (String)m.put("name2", "cappuccino");
		System.out.println("cache name2=cappuccino, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		value = (String)m.put("name2", "cappuccino_new");
		System.out.println("cache name2=cappuccino_new, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		Thread.sleep(6000);
		
		value = (String)m.put("name3", "holmes");
		System.out.println("cache name3=holmes, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		value = (String)m.put("name3", "holmes_new");
		System.out.println("cache name3=holmes_new, return oldvalue:" + value);
		
		System.out.println("lookup:" + m.lookup());
		
		value = (String)m.get("name2");
		System.out.println("get key=name2,return:" + value);
		
		System.out.println("lookup:" + m.lookup());
	}
}
