package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import view.View;

public class Main {
	public static String[] registers = new String[16];
	public static ArrayList<String> instructions = new ArrayList<String>();
	public static String[] regNames = new String[16];
	public static String[] opCodes = new String[16];

	static int pc = 0;
	static String PCString;
	static String currentInstruction;
	static String OPCode;
	static String sr;
	static String tr;
	static String dr;
	static String immediate;
	static String label;
	static String updatedPCString;

	public static void init() {

		String rs = "pc pc2 $a0 $a1 $a2 $a3 " + "$v0 " + "$t0 $t1 $t2 $t3 " + "$s0 $s1 " + "$1 $ra $ra2";
		regNames = rs.split("\\s+");

		String os = "add mult addi or and xor nor slt shl sub beq blt j jal sw lw";
		opCodes = os.split("\\s+");

		for (int i = 0; i < registers.length; i++)
			registers[i] = "0000";
	}

	public static ArrayList<String> readFromFile(String path) {// fetch
		File file = new File(path);
		BufferedReader reader = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
	}

	public static boolean checkOperators(String op) {
		for (int i = 0; i < opCodes.length; i++)
			if (op.equalsIgnoreCase(opCodes[i].toString()))
				return true;
		return false;
	}

	public static boolean checkRegisters(String reg) {
		for (String s : regNames)
			if (s.equalsIgnoreCase(reg))
				return true;
		return false;
	}

	public static boolean checkImmediate(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public static boolean checkIfLabel(String label) {
		if (label.length() > 0)
			if (label.charAt(label.length() - 1) == ':')
				return true;
		return false;
	}

	public static String opCodeconvertToBinary(String opCode) {
		int tmp = 0;
		for (int index = 0; index < opCodes.length; index++)
			if (opCodes[index].equalsIgnoreCase(opCode))
				tmp = index;
		return convertToBinary(tmp, 4);
	}

	public static String regConvertToBinary(String reg) {
		int tmp = 0;
		for (int index = 0; index < regNames.length; index++)
			if (regNames[index].equalsIgnoreCase(reg))
				tmp = index;
		return convertToBinary(tmp, 4);
	}

	public static String convertToBinary(int n, int digits) {
		int a;
		String x = "";
		while (n > 0) {
			a = n % 2;
			x = a + "" + x;
			n = n / 2;
		}
		while (x.length() < digits)
			x = 0 + x;
		return x;
	}

	public static void encode(String path) throws CompileException {
		ArrayList<String> lines = readFromFile(path);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String[] col = line.split("\\s+");
			String binary = "";

			while (line.isEmpty() || checkIfLabel(col[0]) || line.contains("//") || col[0].equalsIgnoreCase("exit")
					|| col[0].equalsIgnoreCase("jal") || col[0].equalsIgnoreCase("j")) {
				if (col[0].equalsIgnoreCase("exit"))
					instructions.add("exit");
				// if(line.isEmpty()) instructions.add(" ");
				if (checkIfLabel(col[0]))
					instructions.add(col[0]);
				if (col[0].equalsIgnoreCase("j") || col[0].equalsIgnoreCase("jal")) {
					if (col.length - col[0].length() == 0)
						throw new CompileException("compile error at line :" + i + " ,col:1: you should use a label");
					if (col[1].isEmpty())
						throw new CompileException("compile error at line :" + i + " ,col:1: you should use a label");
					instructions.add(opCodeconvertToBinary(col[0]) + col[1]);
				}
				i++;
				if (i == lines.size())
					return;
				line = lines.get(i);
				col = line.split("\\s+");
			}

			// check opCode
			if (!checkOperators(col[0]))
				throw new CompileException(
						"compile error at line :" + i + " ,col:0 \nWrong operator: \" " + col[0] + " \"");
			// check Reg1
			if (!checkRegisters(col[1]))
				throw new CompileException(
						"compile error at line :" + i + " ,col:1 \nWrong register: \" " + col[1] + " \"");
			// check reg2
			if (!checkRegisters(col[2]))
				throw new CompileException(
						"compile error at line :" + i + " ,col:2 \nWrong register: \" " + col[2] + " \"");
			binary += opCodeconvertToBinary(col[0]);
			binary += regConvertToBinary(col[1]);
			instructions.add(binary);
			binary = regConvertToBinary(col[2]);
			if (checkImmediate(col[3]))
				binary += convertToBinary(Integer.parseInt(col[3]), 4);
			else if (checkRegisters(col[3]))
				binary += regConvertToBinary(col[3]);
			else
				binary += col[3];

			instructions.add(binary);
		}
	}

	public static void main(String[] args) throws CompileException {
		init();
		String path = "PUT_YOUR_PATH\\test.txt";
		encode(path);

		for (int i = 0; i < registers.length; i++) {
			registers[i] = "0000";
		}

		while (true) {
			PCString = registers[0] + registers[1];
			pc = Integer.parseInt(PCString, 2);
			currentInstruction = instructions.get(pc);
			if (currentInstruction.equals("exit"))
				break;
			if (currentInstruction.contains(":")) {
				pc += 1;
				PCString = decToBinary(pc);
				updatedPCString = repInEight(PCString);
				registers[0] = updatedPCString.substring(0, 4);
				registers[1] = updatedPCString.substring(4);
			} else {
				OPCode = currentInstruction.substring(0, 4);
				currentInstruction = instructions.get(pc);
				if (OPCode.equals("1100") | OPCode.equals("1101")) {
					currentInstruction = instructions.get(pc);
				} else
					currentInstruction = instructions.get(pc) + instructions.get(pc + 1);
				switch (OPCode) {
				case ("0000"):// add
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									+ Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0001"):// mult
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									* Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0010"):// addi
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2) + Integer.parseInt(tr, 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0011"):// or
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									| Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0100"):// and
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									& Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0101"):// xor
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									^ Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0110"):// nor
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							~((Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									| Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2))));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("0111"):// slt
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					if (Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2) < Integer
							.parseInt((registers[Integer.parseInt(tr, 2)]), 2))
						registers[Integer.parseInt(dr, 2)] = "0001";
					else
						registers[Integer.parseInt(dr, 2)] = "0000";
					break;

				case ("1000"):// shl
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					immediate = currentInstruction.substring(12);
					int intImm = Integer.parseInt(immediate, 2);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2) << (intImm % 4));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("1001"):// sub
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					registers[Integer.parseInt(dr, 2)] = decToBinary(
							(Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)
									- Integer.parseInt((registers[Integer.parseInt(tr, 2)]), 2)));
					registers[Integer.parseInt(dr, 2)] = repInFour(registers[Integer.parseInt(dr, 2)]);
					break;

				case ("1010"):// beq
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					if (registers[Integer.parseInt(dr, 2)].equals(registers[Integer.parseInt(sr, 2)])) {
						pc = instructions.indexOf(tr + ":") + 1;
						PCString = decToBinary(pc);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					} else {
						pc += 2;
						PCString = decToBinary(pc);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					}
					break;
				case ("1011"):// blt
					dr = currentInstruction.substring(4, 8);
					sr = currentInstruction.substring(8, 12);
					tr = currentInstruction.substring(12);
					if ((Integer.parseInt((registers[Integer.parseInt(dr, 2)]),
							2) < (Integer.parseInt((registers[Integer.parseInt(sr, 2)]), 2)))) {
						pc = instructions.indexOf(tr + ":") + 1;
						PCString = decToBinary(pc);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					} else {
						pc += 2;
						PCString = decToBinary(pc);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					}
					break;
				case ("1100"):// j
					tr = currentInstruction.substring(4);
					if (tr.equals("ra")) {
						PCString = registers[14] + registers[15];
						pc = Integer.parseInt(PCString, 2);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					} else {
						pc = instructions.indexOf(tr + ":") + 1;
						PCString = decToBinary(pc);
						updatedPCString = repInEight(PCString);
						registers[0] = updatedPCString.substring(0, 4);
						registers[1] = updatedPCString.substring(4);
					}
					break;
				case ("1101"):// jal
					tr = currentInstruction.substring(4);
					int i = Integer.parseInt(PCString, 2) + 1;
					PCString = decToBinary(i);
					updatedPCString = repInEight(PCString);
					registers[14] = updatedPCString.substring(0, 4);
					registers[15] = updatedPCString.substring(4);
					pc = instructions.indexOf(tr + ":") + 1;
					PCString = decToBinary(pc);
					updatedPCString = repInEight(PCString);
					registers[0] = updatedPCString.substring(0, 4);
					registers[1] = updatedPCString.substring(4);
					break;
				case ("1110"):// sw

				case ("1111"):// lw

				}
				if (!(OPCode.equals("1010") | (OPCode.equals("1011")) | (OPCode.equals("1100"))
						| (OPCode.equals("1101")))) {
					pc += 2;
					PCString = decToBinary(pc);
					updatedPCString = repInEight(PCString);
					registers[0] = updatedPCString.substring(0, 4);
					registers[1] = updatedPCString.substring(4);
				}
			}
			if (pc >= instructions.size())
				break;
		}
		new View();
	}

	public static String decToBinary(int n) {
		String x = "";
		while (n > 0) {
			int a = n % 2;
			x = a + x;
			n = n / 2;
		}
		return (x);
	}

	public static String repInFour(String s) {
		while (s.length() < 4) {
			s = "0" + s;
		}
		if (s.length() > 4)
			s.substring(s.length() - 4);
		return (s);
	}

	public static String repInEight(String s) {
		while (s.length() < 8) {
			s = "0" + s;
		}
		if (s.length() > 8)
			s.substring(s.length() - 8);
		return (s);
	}

}
