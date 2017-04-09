import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TestTree {

	public static HashMap<Timestamp, ArrayList<TrajectoryObject>> getDateTrajectoryObjectMap(String fileName) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		System.out.println("Data File Read Begin");
		TrajectoryObject lastObject=null;
		TrajectoryObject currentObject=null;
		HashMap<Timestamp, ArrayList<TrajectoryObject>> dateTrajectoryObjectMap=new HashMap<Timestamp, ArrayList<TrajectoryObject>>();
		try {
			br = new BufferedReader(new FileReader(fileName));
			line = br.readLine();
			System.out.println("Header");
			System.out.println(line);
			while ((line = br.readLine()) != null) {
				String[] trajData = line.split(cvsSplitBy);
				int objectId= Integer.parseInt(trajData[0].trim());
				int trajectoryId=Integer.parseInt(trajData[1].trim());
				Timestamp tbegin=Helpers.parseTime(trajData[2].trim());
				Timestamp tend=Helpers.parseTime(trajData[3].trim());
				double xbegin=Double.parseDouble(trajData[4].trim());
				double xend=Double.parseDouble(trajData[5].trim());
				double ybegin=Double.parseDouble(trajData[6].trim());
				double yend=Double.parseDouble(trajData[7].trim());
				currentObject=new TrajectoryObject(tend,xend,yend,objectId,trajectoryId);
				if(lastObject !=null) {
					if((objectId==lastObject.getObjectId()) && (tbegin==lastObject.getT()) && (xbegin==lastObject.getX()) && (ybegin==lastObject.getY())) {
						currentObject.setTrajectoryId(lastObject.getTrajectoryId());
						lastObject.setNext(currentObject);
						currentObject.setPrevious(lastObject);
					}
				}
				Timestamp dateKey=Helpers.getDateFromTime(currentObject.getT());
				ArrayList<TrajectoryObject> trajectoryObjectList;
				if (dateTrajectoryObjectMap.containsKey(dateKey)) {
					trajectoryObjectList=(ArrayList<TrajectoryObject>)dateTrajectoryObjectMap.get(dateKey);
				} else {
					trajectoryObjectList=new ArrayList<TrajectoryObject>();
					dateTrajectoryObjectMap.put(dateKey, trajectoryObjectList);
				}
				trajectoryObjectList.add(currentObject);
				lastObject=currentObject;
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Data File Read Done");
		return dateTrajectoryObjectMap;
	}
	
	public static void comparePerformance(ArrayList<TrajectoryObject> allData) {
		System.out.println( "Data Size = " + allData.size());
        RTree rTree=new RTree(allData);
        BP3DTree bpTree = new BP3DTree(allData);
        long avgRtreeQueryTime=0;
        long avg3dtreeQueryTime=0;
        int numSamples=1000;
        int errors=0;
        for( int i=0; i < numSamples; i++) {
        	Range someRange=((RTreeNode)rTree.getRoot()).getDataRange().getRandomSubrange(5,5,5);
        	long startTime1 = System.nanoTime();
        	List<MovingObject> l1=rTree.getObjectsInRange(someRange);
        	long endTime1 = System.nanoTime();
        	avgRtreeQueryTime=avgRtreeQueryTime+(endTime1-startTime1);
        	long startTime2 = System.nanoTime();
        	List<MovingObject> l2=bpTree.getObjectsInRange(someRange);
        	long endTime2 = System.nanoTime();
        	avg3dtreeQueryTime=avg3dtreeQueryTime+(endTime2-startTime2);
        	if(l1.size()!=l2.size()) errors=errors+1;
        }
        avgRtreeQueryTime=avgRtreeQueryTime/numSamples;
        avg3dtreeQueryTime=avg3dtreeQueryTime/numSamples;
        System.out.println("Num Errors="+errors);
        System.out.println("RTree RangeQuery: "+avgRtreeQueryTime/1000);
        System.out.println("BP3dTree RangeQuery: "+avg3dtreeQueryTime/1000);
//        rTree.printTree("/home/shikha/work/data/testData/rTree.txt");
//        bpTree.printTree("/home/shikha/work/data/testData/bpTree.txt");
        System.out.println("RTree Stats");
        rTree.printTreeStats();
        System.out.println("BP3DPartition Tree Stats");
        bpTree.printTreeStats();
	}
	public static ArrayList<TrajectoryObject> getNPoints(HashMap<Timestamp, ArrayList<TrajectoryObject>> dateTrajectoryObjectMap, int N) {
		ArrayList<TrajectoryObject>allData = new ArrayList<TrajectoryObject>();
		Iterator<Entry<Timestamp, ArrayList<TrajectoryObject>>> it = dateTrajectoryObjectMap.entrySet().iterator();
		while (it.hasNext() ) {
			ArrayList<TrajectoryObject> thisSet = ((Map.Entry<Timestamp, ArrayList<TrajectoryObject>>)it.next()).getValue();
			if(thisSet.size()+allData.size()<N) allData.addAll(thisSet);
			else {
				int remainingSize=(N-allData.size());
				System.out.println("Remaining Size="+remainingSize+"  ThisSet Size="+ thisSet.size()+"  AllData Size="+allData.size());
				for(int i=0;i<remainingSize ;i++) {
					allData.add(thisSet.get(i));
				}
				System.out.println("AllData Size="+allData.size());
				break;
			}
		}
		return allData;
	}
	public static void main(String[] args) {
		String fileName=args[0];
		System.out.println(fileName);
		HashMap<Timestamp, ArrayList<TrajectoryObject>> dateTrajectoryObjectMap=getDateTrajectoryObjectMap(fileName);
		int[] numPoints = {1024,2048,4096,8192,16384,32768,65536,131072,262144,524288} ;
		for(int i=0;i<numPoints.length;i++) {
			int N= numPoints[i];
			System.out.println("N="+N);
			ArrayList<TrajectoryObject>allData =getNPoints(dateTrajectoryObjectMap, N);
			System.out.println("AllData Size="+allData.size());
			comparePerformance(allData);
			System.out.println("");
		}
		
//		Iterator<Entry<Timestamp, ArrayList<TrajectoryObject>>> it = dateTrajectoryObjectMap.entrySet().iterator();
//		int cnt=0;
//		ArrayList<TrajectoryObject>allData = new ArrayList<TrajectoryObject>();
//		while (it.hasNext() && cnt <1) {
//	         allData.addAll(((Map.Entry<Timestamp, ArrayList<TrajectoryObject>>)it.next()).getValue());
//		}
//		comparePerformance(allData,10,10,10);
//		it = dateTrajectoryObjectMap.entrySet().iterator();
//		while (it.hasNext() && cnt <10) {
//	        Map.Entry<Timestamp, ArrayList<TrajectoryObject>> pairs = (Map.Entry<Timestamp, ArrayList<TrajectoryObject>>)it.next();
//	        comparePerformance(pairs.getValue(),5,5,5);
//	        cnt=cnt+1;
//	    }
		
	}

}
