package com.vietnam.history.statistics;

import java.io.File;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class NKSStatistic extends EntityStatistic{

	public NKSStatistic(String folderPath) {
		super(folderPath);
		
	}
	
	
	public static void main(String[] args) {
		StringBuilder s = new StringBuilder("Thông kê nguồn Người kể sử: \n");
		int totalEntities = 0;
		int totalConnection = 0;
		NKSStatistic nksStat = new NKSStatistic("src/data/NKS/");
		for (String type: EntityStatistic.ENTITY_TYPES) {
			int countEnity =  nksStat.countEntity(type);
			TreeMap<String, Integer> sortedProperties= nksStat.countEntityProperty(type);
			int countEnityProperty = sortedProperties.size();
			Set<String> topProperties = sortedProperties.keySet().stream().limit(10).collect(Collectors.toSet());
			int countConnectable = nksStat.countConnectable(type);
	        s.append("\tSố " + type +": " + countEnity + "\n");
	        s.append("\tTống số thuộc tính của "+ type +":" + countEnityProperty + "\n");
	        s.append("\tCác thuộc tính chính: ").append(topProperties).append("\n\n");
	        totalEntities += countEnity;
	        totalConnection += countConnectable;
		}
		s.append("\tTổng số thực thể: "+ totalEntities + "\n");
		s.append("\tTổng số liên kết: "+ totalConnection + "\n\n");
		System.out.println(s);
	}
}
