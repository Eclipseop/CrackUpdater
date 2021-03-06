package org.objectweb.asm.commons.cfg.tree.node;

import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class NumberNode extends AbstractNode {

	public NumberNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
		super(tree, insn, collapsed, producing);
	}
	
	public boolean isInt() {
		AbstractInsnNode insn = insn();
		int op = insn.opcode();
		switch (op) {
			case NEWARRAY:
			case BIPUSH:
			case SIPUSH:
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5:
			case LCONST_0:
			case LCONST_1:
				return true;
			case FCONST_0:
			case FCONST_1:
			case FCONST_2: 
			case DCONST_0:
			case DCONST_1: 
				return false;
			case LDC: 
				Object cst = ((LdcInsnNode) insn).cst;
				if(cst instanceof Integer) {
					return true;
				} else if(cst instanceof Long) {
					return true;
				} else if(cst instanceof Float) {
					return false;
				} else if(cst instanceof Double) {
					return false;
				}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}

	public long longNumber() {
		AbstractInsnNode insn = insn();
		int op = insn.opcode();
		switch (op) {
			case NEWARRAY:
			case BIPUSH:
			case SIPUSH: {
				return ((IntInsnNode) insn).operand;
			}
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5: {
				return op - ICONST_0;
			}
			case LCONST_0:
			case LCONST_1: {
				return op - LCONST_0;
			}
			case FCONST_0:
			case FCONST_1:
			case FCONST_2: {
				return op - FCONST_0;
			}
			case DCONST_0:
			case DCONST_1: {
				return op - DCONST_0;
			}
			case LDC: {
				Object cst = ((LdcInsnNode) insn).cst;
				if (cst instanceof Number) {
					return ((Number)cst).longValue();
				}
			}
			default: {
				return -1;
			}
		}
	}

	public Class<?> type() {
		AbstractInsnNode insn = insn();
		int op = insn.opcode();
		switch (op) {
			case NEWARRAY:
			case BIPUSH:
			case SIPUSH:
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5:
				return Integer.TYPE;
			case LCONST_0:
			case LCONST_1: {
				return Long.TYPE;
			}
			case FCONST_0:
			case FCONST_1:
			case FCONST_2: 
				return Float.TYPE;
			case DCONST_0:
			case DCONST_1: 
				return Double.TYPE;
			case LDC: 
				Object cst = ((LdcInsnNode) insn).cst;
				if(cst instanceof Integer) {
					return Integer.TYPE;
				} else if(cst instanceof Long) {
					return Long.TYPE;
				} else if(cst instanceof Float) {
					return Float.TYPE;
				} else if(cst instanceof Double) {
					return Double.TYPE;
				}
			default: {
				return null;
			}
		}
	}

	public int number() {
		AbstractInsnNode insn = insn();
		int op = insn.opcode();
		switch (op) {
			case NEWARRAY:
			case BIPUSH:
			case SIPUSH: {
				return ((IntInsnNode) insn).operand;
			}
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5: {
				return op - ICONST_0;
			}
			case LCONST_0:
			case LCONST_1: {
				return op - LCONST_0;
			}
			case FCONST_0:
			case FCONST_1:
			case FCONST_2: {
				return op - FCONST_0;
			}
			case DCONST_0:
			case DCONST_1: {
				return op - DCONST_0;
			}
			case LDC: {
				Object cst = ((LdcInsnNode) insn).cst;
				if (cst instanceof Number) {
					return ((Number) cst).intValue();
				}
			}
			default: {
				return -1;
			}
		}
	}
	
	public Number nNumber() {
		AbstractInsnNode insn = insn();
		int op = insn.opcode();
		switch (op) {
			case NEWARRAY:
			case BIPUSH:
			case SIPUSH: {
				return ((IntInsnNode) insn).operand;
			}
			case ICONST_M1:
			case ICONST_0:
			case ICONST_1:
			case ICONST_2:
			case ICONST_3:
			case ICONST_4:
			case ICONST_5: {
				return op - ICONST_0;
			}
			case LCONST_0:
			case LCONST_1: {
				return op - LCONST_0;
			}
			case FCONST_0:
			case FCONST_1:
			case FCONST_2: {
				return op - FCONST_0;
			}
			case DCONST_0:
			case DCONST_1: {
				return op - DCONST_0;
			}
			case LDC: {
				Object cst = ((LdcInsnNode) insn).cst;
				if (cst instanceof Number) {
					return (Number) cst;
				} else {
					throw new IllegalArgumentException();
				}
			}
			default: {
				throw new IllegalArgumentException();
			}
		}
	}

	public void setNumber(int number) {
		AbstractInsnNode ain = insn();
		if (ain instanceof IntInsnNode) {
			((IntInsnNode) insn()).operand = number;
			((IntInsnNode) ain).operand = number;
		} else if (ain instanceof LdcInsnNode) {
			((LdcInsnNode) insn()).cst = number;
			((LdcInsnNode) ain).cst = number;
		}
	}

	public void setNumber(Number num) {
		AbstractInsnNode ain = insn();
		if(!(ain instanceof LdcInsnNode)) {
			setInstruction(new LdcInsnNode(num));
		} else{
			((LdcInsnNode) ain).cst = num;
		}
	}

	@Override
	public String toString() {
		return insn().toString();
	}
}