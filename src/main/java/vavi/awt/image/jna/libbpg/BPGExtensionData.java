package vavi.awt.image.jna.libbpg;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : libbpg.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class BPGExtensionData extends Structure {
	/**
	 * @see LibbpgLibrary.BPGExtensionTagEnum
	 * C type : BPGExtensionTagEnum
	 */
	public int tag;
	public int buf_len;
	/** C type : uint8_t* */
	public Pointer buf;
	/** C type : BPGExtensionData* */
	public BPGExtensionData.ByReference next;
	public BPGExtensionData() {
		super();
	}
	@Override protected List<String> getFieldOrder() {
		return Arrays.asList("tag", "buf_len", "buf", "next");
	}
	/**
	 * @param tag @see BPGExtensionTagEnum<br>
	 * C type : BPGExtensionTagEnum<br>
	 * @param buf C type : uint8_t*<br>
	 * @param next C type : BPGExtensionData*
	 */
	public BPGExtensionData(int tag, int buf_len, Pointer buf, BPGExtensionData.ByReference next) {
		super();
		this.tag = tag;
		this.buf_len = buf_len;
		this.buf = buf;
		this.next = next;
	}
	public BPGExtensionData(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends BPGExtensionData implements Structure.ByReference {
	}
	public static class ByValue extends BPGExtensionData implements Structure.ByValue {
	}
}
