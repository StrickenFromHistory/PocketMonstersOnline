package org.pokenet.routes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadRoutes {
	public static void main(String[] args)
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("routes.txt"));
			String line = br.readLine();
			int x = -50;
			ArrayList<RouteBean> routes = new ArrayList<RouteBean>();
			while ( (line = br.readLine()) != null )
			{
//				System.out.println(line);				
				String[] parse = line.split("	");
				for(int i=1;i<parse.length;i++){
					int y = i-51;
					if(!parse[i].equals("")){
						RouteBean route = new RouteBean(x,y,parse[i]);
						routes.add(route);
					}
				}
				x++;
			}
			for(int i=0;i<routes.size();i++)
//				for(x = -50;x<51;x++)
//					for(int y =-50;y<51;y++)
				System.out.println(routes.get(i).getMapname()+" "+routes.get(i).getX()+","+routes.get(i).getY());

			
		}
		catch (FileNotFoundException e)
		{
			// can be thrown when creating the FileReader/BufferedReader
			// deal with the exception
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// can be thrown by br.readLine()
			// deal with the exception
			e.printStackTrace();
		}
	}
}