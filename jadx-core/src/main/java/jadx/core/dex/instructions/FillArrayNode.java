package jadx.core.dex.instructions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.dx.io.instructions.FillArrayDataPayloadDecodedInstruction;

import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.PrimitiveType;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.utils.exceptions.JadxRuntimeException;

public final class FillArrayNode extends InsnNode {

	private final Object data;
	private final int size;
	private ArgType elemType;

	public FillArrayNode(int resReg, FillArrayDataPayloadDecodedInstruction payload) {
		super(InsnType.FILL_ARRAY, 1);
		ArgType elType;
		switch (payload.getElementWidthUnit()) {
			case 1:
				elType = ArgType.unknown(PrimitiveType.BOOLEAN, PrimitiveType.BYTE);
				break;
			case 2:
				elType = ArgType.unknown(PrimitiveType.SHORT, PrimitiveType.CHAR);
				break;
			case 4:
				elType = ArgType.unknown(PrimitiveType.INT, PrimitiveType.FLOAT);
				break;
			case 8:
				elType = ArgType.unknown(PrimitiveType.LONG, PrimitiveType.DOUBLE);
				break;

			default:
				throw new JadxRuntimeException("Unknown array element width: " + payload.getElementWidthUnit());
		}
		addArg(InsnArg.reg(resReg, ArgType.array(elType)));

		this.data = payload.getData();
		this.size = payload.getSize();
		this.elemType = elType;
	}

	public Object getData() {
		return data;
	}

	public int getSize() {
		return size;
	}

	public ArgType getElementType() {
		return elemType;
	}

	public List<LiteralArg> getLiteralArgs(ArgType type) {
		List<LiteralArg> list = new ArrayList<>(size);
		Object array = data;
		if (array instanceof int[]) {
			for (int b : (int[]) array) {
				list.add(InsnArg.lit(b, type));
			}
		} else if (array instanceof byte[]) {
			for (byte b : (byte[]) array) {
				list.add(InsnArg.lit(b, type));
			}
		} else if (array instanceof short[]) {
			for (short b : (short[]) array) {
				list.add(InsnArg.lit(b, type));
			}
		} else if (array instanceof long[]) {
			for (long b : (long[]) array) {
				list.add(InsnArg.lit(b, type));
			}
		} else {
			throw new JadxRuntimeException("Unknown type: " + data.getClass() + ", expected: " + type);
		}
		return list;
	}

	@Override
	public boolean isSame(InsnNode obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FillArrayNode) || !super.isSame(obj)) {
			return false;
		}
		FillArrayNode other = (FillArrayNode) obj;
		return elemType.equals(other.elemType) && data == other.data;
	}

	public String dataToString() {
		if (data instanceof int[]) {
			return Arrays.toString((int[]) data);
		}
		if (data instanceof short[]) {
			return Arrays.toString((short[]) data);
		}
		if (data instanceof byte[]) {
			return Arrays.toString((byte[]) data);
		}
		if (data instanceof long[]) {
			return Arrays.toString((long[]) data);
		}
		return "?";
	}

	@Override
	public String toString() {
		return super.toString() + ", data: " + dataToString();
	}
}
