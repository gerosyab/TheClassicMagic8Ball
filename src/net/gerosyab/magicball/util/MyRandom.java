package net.gerosyab.magicball.util;

import java.util.Random;

import net.gerosyab.magicball.data.StaticData;

/*
 * Helper class for generating msg index number randomly
 * it has own history so same number will not be picked up before generating more than history size times
 */
public class MyRandom {
	private static Random random = new Random(System.currentTimeMillis());
	private static int[] history = {-1, -1, -1, -1, -1};
	private static int curIndex = -1;
	
	public static int getNum(){
		int result = 0;
		
		MyLog.d("MyRandom", "history : " + history[0] + ", " + history[1] + ", " + history[2] + ", " +  history[3] + ", " + history[4]);
		MyLog.d("MyRandom", "curIndex : " + curIndex);
		
		curIndex = getNextIndex();
		
		MyLog.d("MyRandom", "getNextIndex() : " + curIndex);
		
		
//		keep the picked numbers (size of history array) to prevent re-picked up		
		boolean generated = false;
		do{
			result = random.nextInt(StaticData.msgID.length);
			generated = true;
			
			for(int i = 0; i < history.length; i++){
				if(result == history[i]){
					generated = false;
					break;
				}
			}
		} while (!generated);
		
		history[curIndex] = result;
		
		return result;
	}
	
	// index picker for history array
	private static int getNextIndex(){
		int nextIndex = curIndex + 1;
		if(nextIndex >= history.length){
			nextIndex = 0;
		}
		return nextIndex;
	}
	
}
