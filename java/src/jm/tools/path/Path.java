package jm.tools.path;

import java.io.File;

/**
 * 路径工具类，从eclipse中移植过来的
 * @author yjm
 *
 */
public class Path implements Cloneable {
	private static final int HAS_LEADING = 1;
	private static final int IS_UNC = 2;
	private static final int HAS_TRAILING = 4;
	private static final int ALL_SEPARATORS = 7;
	private static final String EMPTY_STRING = "";
	private static final String[] NO_SEGMENTS = new String[0];

	public static final Path EMPTY = new Path("");
	private static final int HASH_MASK = -5;
	private static final String ROOT_STRING = "/";
	public static final Path ROOT = new Path("/");

	private static final boolean WINDOWS = File.separatorChar == '\\';

	private String device = null;
	private String[] segments;
	private int separators;

	public static Path fromOSString(String pathString) {
		return new Path(pathString);
	}

	public static Path fromPortableString(String pathString) {
		int firstMatch = pathString.indexOf(':') + 1;

		if (firstMatch <= 0) {
			return new Path().initialize(null, pathString);
		}
		String devicePart = null;
		int pathLength = pathString.length();
		if ((firstMatch == pathLength)
				|| (pathString.charAt(firstMatch) != ':')) {
			devicePart = pathString.substring(0, firstMatch);
			pathString = pathString.substring(firstMatch, pathLength);
		}

		if (pathString.indexOf(':') == -1) {
			return new Path().initialize(devicePart, pathString);
		}
		char[] chars = pathString.toCharArray();
		int readOffset = 0;
		int writeOffset = 0;
		int length = chars.length;
		while (readOffset < length) {
			if (chars[readOffset] == ':') {
				readOffset++;
				if (readOffset >= length)
					break;
			}
			chars[(writeOffset++)] = chars[(readOffset++)];
		}
		return new Path().initialize(devicePart, new String(chars, 0,
				writeOffset));
	}

	private Path() {
	}

	public Path(String fullPath) {
		String devicePart = null;
		if (WINDOWS) {
			fullPath = fullPath.indexOf('\\') == -1 ? fullPath : fullPath
					.replace('\\', '/');

			int i = fullPath.indexOf(':');
			if (i != -1) {
				int start = fullPath.charAt(0) == '/' ? 1 : 0;
				devicePart = fullPath.substring(start, i + 1);
				fullPath = fullPath.substring(i + 1, fullPath.length());
			}
		}
		initialize(devicePart, fullPath);
	}

	public Path(String device, String path) {
		if (WINDOWS) {
			path = path.indexOf('\\') == -1 ? path : path.replace('\\', '/');
		}
		initialize(device, path);
	}

	private Path(String device, String[] segments, int _separators) {
		this.segments = segments;
		this.device = device;

		this.separators = (computeHashCode() << 3 | _separators & 0x7);
	}

	public Path addFileExtension(String extension) {
		if ((isRoot()) || (isEmpty()) || (hasTrailingSeparator()))
			return this;
		int len = this.segments.length;
		String[] newSegments = new String[len];
		System.arraycopy(this.segments, 0, newSegments, 0, len - 1);
		newSegments[(len - 1)] = (this.segments[(len - 1)] + '.' + extension);
		return new Path(this.device, newSegments, this.separators);
	}

	public Path addTrailingSeparator() {
		if ((hasTrailingSeparator()) || (isRoot())) {
			return this;
		}

		if (isEmpty()) {
			return new Path(this.device, this.segments, 1);
		}
		return new Path(this.device, this.segments, this.separators | 0x4);
	}

	public Path append(Path tail) {
		if ((tail == null) || (tail.segmentCount() == 0)) {
			return this;
		}
		if (isEmpty())
			return tail.setDevice(this.device).makeRelative().makeUNC(isUNC());
		if (isRoot()) {
			return tail.setDevice(this.device).makeAbsolute().makeUNC(isUNC());
		}

		int myLen = this.segments.length;
		int tailLen = tail.segmentCount();
		String[] newSegments = new String[myLen + tailLen];
		System.arraycopy(this.segments, 0, newSegments, 0, myLen);
		for (int i = 0; i < tailLen; i++) {
			newSegments[(myLen + i)] = tail.segment(i);
		}

		Path result = new Path(this.device, newSegments, this.separators & 0x3
				| (tail.hasTrailingSeparator() ? 4 : 0));
		String tailFirstSegment = newSegments[myLen];
		if ((tailFirstSegment.equals("..")) || (tailFirstSegment.equals("."))) {
			result.canonicalize();
		}
		return result;
	}

	public Path append(String tail) {
		if ((tail.indexOf('/') == -1) && (tail.indexOf("\\") == -1)
				&& (tail.indexOf(':') == -1)) {
			int tailLength = tail.length();
			if (tailLength < 3) {
				if ((tailLength == 0) || (".".equals(tail))) {
					return this;
				}
				if ("..".equals(tail)) {
					return removeLastSegments(1);
				}
			}
			int myLen = this.segments.length;
			String[] newSegments = new String[myLen + 1];
			System.arraycopy(this.segments, 0, newSegments, 0, myLen);
			newSegments[myLen] = tail;
			return new Path(this.device, newSegments,
					this.separators & 0xFFFFFFFB);
		}

		return append(new Path(tail));
	}

	private boolean canonicalize() {
		int i = 0;
		for (int max = this.segments.length; i < max; i++) {
			String segment = this.segments[i];
			if ((segment.charAt(0) != '.')
					|| ((!segment.equals("..")) && (!segment.equals("."))))
				continue;
			collapseParentReferences();

			if (this.segments.length == 0) {
				this.separators &= 3;
			}
			this.separators = (this.separators & 0x7 | computeHashCode() << 3);
			return true;
		}

		return false;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException localCloneNotSupportedException) {
		}
		return null;
	}

	private void collapseParentReferences() {
		int segmentCount = this.segments.length;
		String[] stack = new String[segmentCount];
		int stackPointer = 0;
		for (int i = 0; i < segmentCount; i++) {
			String segment = this.segments[i];
			if (segment.equals("..")) {
				if (stackPointer == 0) {
					if (!isAbsolute()) {
						stack[(stackPointer++)] = segment;
					}
				} else if ("..".equals(stack[(stackPointer - 1)]))
					stack[(stackPointer++)] = "..";
				else {
					stackPointer--;
				}

			} else if ((!segment.equals(".")) || (segmentCount == 1)) {
				stack[(stackPointer++)] = segment;
			}
		}
		if (stackPointer == segmentCount) {
			return;
		}
		String[] newSegments = new String[stackPointer];
		System.arraycopy(stack, 0, newSegments, 0, stackPointer);
		this.segments = newSegments;
	}

	private String collapseSlashes(String path) {
		int length = path.length();

		if (length < 3) {
			return path;
		}

		if (path.indexOf("//", 1) == -1) {
			return path;
		}
		char[] result = new char[path.length()];
		int count = 0;
		boolean hasPrevious = false;
		char[] characters = path.toCharArray();
		for (int index = 0; index < characters.length; index++) {
			char c = characters[index];
			if (c == '/') {
				if (hasPrevious) {
					if ((this.device == null) && (index == 1)) {
						result[count] = c;
						count++;
					}
				} else {
					hasPrevious = true;
					result[count] = c;
					count++;
				}
			} else {
				hasPrevious = false;
				result[count] = c;
				count++;
			}
		}
		return new String(result, 0, count);
	}

	private int computeHashCode() {
		int hash = this.device == null ? 17 : this.device.hashCode();
		int segmentCount = this.segments.length;
		for (int i = 0; i < segmentCount; i++) {
			hash = hash * 37 + this.segments[i].hashCode();
		}
		return hash;
	}

	private int computeLength() {
		int length = 0;
		if (this.device != null)
			length += this.device.length();
		if ((this.separators & 0x1) != 0)
			length++;
		if ((this.separators & 0x2) != 0) {
			length++;
		}
		int max = this.segments.length;
		if (max > 0) {
			for (int i = 0; i < max; i++) {
				length += this.segments[i].length();
			}

			length += max - 1;
		}
		if ((this.separators & 0x4) != 0)
			length++;
		return length;
	}

	private int computeSegmentCount(String path) {
		int len = path.length();
		if ((len == 0) || ((len == 1) && (path.charAt(0) == '/'))) {
			return 0;
		}
		int count = 1;
		int prev = -1;
		int i;
		while ((i = path.indexOf('/', prev + 1)) != -1) {
			// int i;
			if ((i != prev + 1) && (i != len)) {
				count++;
			}
			prev = i;
		}
		if (path.charAt(len - 1) == '/') {
			count--;
		}
		return count;
	}

	private String[] computeSegments(String path) {
		int segmentCount = computeSegmentCount(path);
		if (segmentCount == 0)
			return NO_SEGMENTS;
		String[] newSegments = new String[segmentCount];
		int len = path.length();

		int firstPosition = path.charAt(0) == '/' ? 1 : 0;

		if ((firstPosition == 1) && (len > 1) && (path.charAt(1) == '/'))
			firstPosition = 2;
		int lastPosition = path.charAt(len - 1) != '/' ? len - 1 : len - 2;

		int next = firstPosition;
		for (int i = 0; i < segmentCount; i++) {
			int start = next;
			int end = path.indexOf('/', next);
			if (end == -1)
				newSegments[i] = path.substring(start, lastPosition + 1);
			else {
				newSegments[i] = path.substring(start, end);
			}
			next = end + 1;
		}
		return newSegments;
	}

	private void encodeSegment(String string, StringBuffer buf) {
		int len = string.length();
		for (int i = 0; i < len; i++) {
			char c = string.charAt(i);
			buf.append(c);
			if (c == ':')
				buf.append(':');
		}
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Path))
			return false;
		Path target = (Path) obj;

		if ((this.separators & 0xFFFFFFFB) != (target.separators & 0xFFFFFFFB))
			return false;
		String[] targetSegments = target.segments;
		int i = this.segments.length;

		if (i != targetSegments.length)
			return false;
		do {
			if (!this.segments[i].equals(targetSegments[i]))
				return false;
			i--;
		} while (i >= 0);

		return (this.device == target.device)
				|| ((this.device != null) && (this.device.equals(target.device)));
	}

	public String getDevice() {
		return this.device;
	}

	public String getFileExtension() {
		if (hasTrailingSeparator()) {
			return null;
		}
		String lastSegment = lastSegment();
		if (lastSegment == null) {
			return null;
		}
		int index = lastSegment.lastIndexOf('.');
		if (index == -1) {
			return null;
		}
		return lastSegment.substring(index + 1);
	}

	public int hashCode() {
		return this.separators & 0xFFFFFFFB;
	}

	public boolean hasTrailingSeparator() {
		return (this.separators & 0x4) != 0;
	}

	private Path initialize(String deviceString, String path) {
		if (path == null) {
			throw new RuntimeException("argument path is null");
		}
		this.device = deviceString;

		path = collapseSlashes(path);
		int len = path.length();

		if (len < 2) {
			if ((len == 1) && (path.charAt(0) == '/'))
				this.separators = 1;
			else
				this.separators = 0;
		} else {
			boolean hasLeading = path.charAt(0) == '/';
			boolean isUNC = (hasLeading) && (path.charAt(1) == '/');

			boolean hasTrailing = ((!isUNC) || (len != 2))
					&& (path.charAt(len - 1) == '/');
			this.separators = (hasLeading ? 1 : 0);
			if (isUNC)
				this.separators |= 2;
			if (hasTrailing) {
				this.separators |= 4;
			}
		}
		this.segments = computeSegments(path);
		if (!canonicalize()) {
			this.separators = (this.separators & 0x7 | computeHashCode() << 3);
		}
		return this;
	}

	public boolean isAbsolute() {
		return (this.separators & 0x1) != 0;
	}

	public boolean isEmpty() {
		return (this.segments.length == 0) && ((this.separators & 0x7) != 1);
	}

	public boolean isPrefixOf(Path anotherPath) {
		if (this.device == null) {
			if (anotherPath.getDevice() != null) {
				return false;
			}
		} else if (!this.device.equalsIgnoreCase(anotherPath.getDevice())) {
			return false;
		}

		if ((isEmpty()) || ((isRoot()) && (anotherPath.isAbsolute()))) {
			return true;
		}
		int len = this.segments.length;
		if (len > anotherPath.segmentCount()) {
			return false;
		}
		for (int i = 0; i < len; i++) {
			if (!this.segments[i].equals(anotherPath.segment(i)))
				return false;
		}
		return true;
	}

	public boolean isRoot() {
		return (this == ROOT)
				|| ((this.segments.length == 0) && ((this.separators & 0x7) == 1));
	}

	public boolean isUNC() {
		if (this.device != null)
			return false;
		return (this.separators & 0x2) != 0;
	}

	public boolean isValidPath(String path) {
		Path test = new Path(path);
		int i = 0;
		for (int max = test.segmentCount(); i < max; i++)
			if (!isValidSegment(test.segment(i)))
				return false;
		return true;
	}

	public boolean isValidSegment(String segment) {
		int size = segment.length();
		if (size == 0)
			return false;
		for (int i = 0; i < size; i++) {
			char c = segment.charAt(i);
			if (c == '/')
				return false;
			if ((WINDOWS) && ((c == '\\') || (c == ':')))
				return false;
		}
		return true;
	}

	public String lastSegment() {
		int len = this.segments.length;
		return len == 0 ? null : this.segments[(len - 1)];
	}

	public Path makeAbsolute() {
		if (isAbsolute()) {
			return this;
		}
		Path result = new Path(this.device, this.segments,
				this.separators | 0x1);

		if (result.segmentCount() > 0) {
			String first = result.segment(0);
			if ((first.equals("..")) || (first.equals("."))) {
				result.canonicalize();
			}
		}
		return result;
	}

	public Path makeRelative() {
		if (!isAbsolute()) {
			return this;
		}
		return new Path(this.device, this.segments, this.separators & 0x4);
	}

	public Path makeUNC(boolean toUNC) {
		if (!(toUNC ^ isUNC())) {
			return this;
		}
		int newSeparators = this.separators;
		if (toUNC) {
			newSeparators |= 3;
		} else {
			newSeparators &= 5;
		}
		return new Path(toUNC ? null : this.device, this.segments,
				newSeparators);
	}

	public int matchingFirstSegments(Path anotherPath) {
		if (anotherPath == null) {
			throw new RuntimeException("argument anotherPath is null");
		}
		int anotherPathLen = anotherPath.segmentCount();
		int max = Math.min(this.segments.length, anotherPathLen);
		int count = 0;
		for (int i = 0; i < max; i++) {
			if (!this.segments[i].equals(anotherPath.segment(i))) {
				return count;
			}
			count++;
		}
		return count;
	}

	public Path removeFileExtension() {
		String extension = getFileExtension();
		if ((extension == null) || (extension.equals(""))) {
			return this;
		}
		String lastSegment = lastSegment();
		int index = lastSegment.lastIndexOf(extension) - 1;
		return removeLastSegments(1).append(lastSegment.substring(0, index));
	}

	public Path removeFirstSegments(int count) {
		if (count == 0)
			return this;
		if (count >= this.segments.length) {
			return new Path(this.device, NO_SEGMENTS, 0);
		}
		// Assert.isLegal(count > 0);
		int newSize = this.segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, count, newSegments, 0, newSize);

		return new Path(this.device, newSegments, this.separators & 0x4);
	}

	public Path removeLastSegments(int count) {
		if (count == 0)
			return this;
		if (count >= this.segments.length) {
			return new Path(this.device, NO_SEGMENTS, this.separators & 0x3);
		}
		// Assert.isLegal(count > 0);
		int newSize = this.segments.length - count;
		String[] newSegments = new String[newSize];
		System.arraycopy(this.segments, 0, newSegments, 0, newSize);
		return new Path(this.device, newSegments, this.separators);
	}

	public Path removeTrailingSeparator() {
		if (!hasTrailingSeparator()) {
			return this;
		}
		return new Path(this.device, this.segments, this.separators & 0x3);
	}

	public String segment(int index) {
		if (index >= this.segments.length)
			return null;
		return this.segments[index];
	}

	public int segmentCount() {
		return this.segments.length;
	}

	public String[] segments() {
		String[] segmentCopy = new String[this.segments.length];
		System
				.arraycopy(this.segments, 0, segmentCopy, 0,
						this.segments.length);
		return segmentCopy;
	}

	public Path setDevice(String value) {
		if (value != null) {
			// Assert.isTrue(value.indexOf(':') == value.length() - 1,
			// "Last character should be the device separator");
		}

		if ((value == this.device)
				|| ((value != null) && (value.equals(this.device)))) {
			return this;
		}
		return new Path(value, this.segments, this.separators);
	}

	public File toFile() {
		return new File(toOSString());
	}

	public String toOSString() {
		int resultSize = computeLength();
		if (resultSize <= 0)
			return "";
		char FILE_SEPARATOR = File.separatorChar;
		char[] result = new char[resultSize];
		int offset = 0;
		if (this.device != null) {
			int size = this.device.length();
			this.device.getChars(0, size, result, offset);
			offset += size;
		}
		if ((this.separators & 0x1) != 0)
			result[(offset++)] = FILE_SEPARATOR;
		if ((this.separators & 0x2) != 0)
			result[(offset++)] = FILE_SEPARATOR;
		int len = this.segments.length - 1;
		if (len >= 0) {
			for (int i = 0; i < len; i++) {
				int size = this.segments[i].length();
				this.segments[i].getChars(0, size, result, offset);
				offset += size;
				result[(offset++)] = FILE_SEPARATOR;
			}

			int size = this.segments[len].length();
			this.segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if ((this.separators & 0x4) != 0)
			result[(offset++)] = FILE_SEPARATOR;
		return new String(result);
	}

	public String toPortableString() {
		int resultSize = computeLength();
		if (resultSize <= 0)
			return "";
		StringBuffer result = new StringBuffer(resultSize);
		if (this.device != null)
			result.append(this.device);
		if ((this.separators & 0x1) != 0)
			result.append('/');
		if ((this.separators & 0x2) != 0)
			result.append('/');
		int len = this.segments.length;

		for (int i = 0; i < len; i++) {
			if (this.segments[i].indexOf(':') >= 0)
				encodeSegment(this.segments[i], result);
			else
				result.append(this.segments[i]);
			if ((i < len - 1) || ((this.separators & 0x4) != 0))
				result.append('/');
		}
		return result.toString();
	}

	public String toString() {
		int resultSize = computeLength();
		if (resultSize <= 0)
			return "";
		char[] result = new char[resultSize];
		int offset = 0;
		if (this.device != null) {
			int size = this.device.length();
			this.device.getChars(0, size, result, offset);
			offset += size;
		}
		if ((this.separators & 0x1) != 0)
			result[(offset++)] = '/';
		if ((this.separators & 0x2) != 0)
			result[(offset++)] = '/';
		int len = this.segments.length - 1;
		if (len >= 0) {
			for (int i = 0; i < len; i++) {
				int size = this.segments[i].length();
				this.segments[i].getChars(0, size, result, offset);
				offset += size;
				result[(offset++)] = '/';
			}

			int size = this.segments[len].length();
			this.segments[len].getChars(0, size, result, offset);
			offset += size;
		}
		if ((this.separators & 0x4) != 0)
			result[(offset++)] = '/';
		return new String(result);
	}

	public Path uptoSegment(int count) {
		if (count == 0)
			return new Path(this.device, NO_SEGMENTS, this.separators & 0x3);
		if (count >= this.segments.length)
			return this;
		// Assert.isTrue(count > 0, "Invalid parameter to Path.uptoSegment");
		String[] newSegments = new String[count];
		System.arraycopy(this.segments, 0, newSegments, 0, count);
		return new Path(this.device, newSegments, this.separators);
	}
	
	public static void main(String[] args) {
		Path path = new Path("c:/sdf\\").append("temp").append("temp1");
		System.out.println(path.toOSString());
	}
}
