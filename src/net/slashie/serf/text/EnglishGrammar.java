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
}
