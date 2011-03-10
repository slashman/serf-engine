package net.slashie.serf.text;

import net.slashie.utils.Util;

public class SimpleEnglishNameGenerator implements EnglishNameGenerator{

	@Override
	public String generateFullName(boolean sex) {
		int nameSyllabes = Util.rand(2, 2);
		int lastNameSyllabes = Util.rand(2, 3);
		String name = "";
		for (int i = 0; i < nameSyllabes; i++){
			name += Util.randomElementOf(NAME_SYLLABES[i]);
		}
		
		String lastname = "";
		for (int i = 0; i < lastNameSyllabes; i++){
			lastname += Util.randomElementOf(LAST_NAME_SYLLABES[i]);
		}
		return name+" "+ lastname;
	}
	
	String [][] NAME_SYLLABES = new String [][]{
		{
			"Ne", "E","A","Guy","Da","Ke","Je", "Kor",
		},
		{
			"il", "rik","llan","rren","lly","ssie", "nel","an"
		}
	};
	
	String [][] LAST_NAME_SYLLABES = new String [][]{
			{
				"Co", "Lo","Ka","Que","Ro","Ke","Co"
			},
			{
				"lum", "nes","no","ve","wen","llings"
			},
			{
				"bus", "de","do","sie"
			}
		};
	
	public static void main(String[] args){
		for (int i = 0; i < 10; i++)
			System.out.println(new SimpleEnglishNameGenerator().generateFullName(true));
	}

}
