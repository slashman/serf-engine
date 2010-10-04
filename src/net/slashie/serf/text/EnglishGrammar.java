package net.slashie.serf.text;

public class EnglishGrammar {
	public static String a(String fullDescription) {
		char firstCharacter = fullDescription.toCharArray()[0];
		switch (firstCharacter){
		case 'a': case 'e': case 'i': case 'o': case 'u':
		case 'A': case 'E': case 'I': case 'O': case 'U':
			return "An";
		default:
			return "A";
		}
	}

	public static String plural(String text, Integer quantity) {
		if (quantity == 1){
			return text;
		} 
		if (text.endsWith("i")){
			return text+"es";
		}
		if (text.endsWith("y") && text.length() > 1){
			return text.substring(0, text.length()-1)+"ies";
		}
		return text+"s";
	}
}
