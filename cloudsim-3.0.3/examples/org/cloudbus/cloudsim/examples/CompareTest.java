package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBrokerACO;
import org.cloudbus.cloudsim.DatacenterBrokerGreedy;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class CompareTest {
	//一个任务列表
	private static List<Cloudlet> cloudletListACO;
	private static List<Cloudlet> cloudletListGreedy;
	private static int numCloudlet = 100;
		
	//虚拟机列表
	private static List<Vm> vmlistACO;
	private static List<Vm> vmlistGreedy;
	private static int numVm = 20;

	public static void main(String[] args) {
		Log.printLine("Starting TestExample...");
		int num_user=1;
		Calendar calendar=Calendar.getInstance();
		boolean trace_flag=false;
		
		CloudSim.init(num_user, calendar, trace_flag);
		
		DatacenterBrokerACO brokerACO = createBrokerACO();
		DatacenterBrokerGreedy brokerGreedy = createBrokerGreedy();
		int brokerACOId=brokerACO.getId();
		int brokerGreedyId=brokerGreedy.getId();
		
		int []mips=new int[numVm];
		mips=random(numVm,100,300);
		
		int []length=new int[numCloudlet];
		length=random(numCloudlet,10000,50000);
		
		long size=10000;
		int ram=2048;
		long bw=10000;
		int pesNumber=1;
		String vmm="xen";
		
//		开始添加Vm
		vmlistACO = new ArrayList<Vm>();
		for(int i=0;i<numVm;i++){
			vmlistACO.add(new Vm(i,brokerACOId,mips[i], pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared()));
		}
		brokerACO.submitVmList(vmlistACO);
		
		vmlistGreedy = new ArrayList<Vm>();
		for(int i=0;i<numVm;i++){
			vmlistGreedy.add(new Vm(i,brokerGreedyId,mips[i], pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared()));
		}
		brokerGreedy.submitVmList(vmlistGreedy);
		
		long fileSize=300;
		long outputSize=300;
		UtilizationModel model=new UtilizationModelFull();
		
		cloudletListACO=new ArrayList<Cloudlet>();
		for (int i = 0; i < numCloudlet; i++) {
			Cloudlet cloudlet=new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, model, model, model);
			cloudlet.setUserId(brokerACOId);
			cloudletListACO.add(cloudlet);
		}
		brokerACO.submitCloudletList(cloudletListACO);
		
		cloudletListGreedy=new ArrayList<Cloudlet>();
		for (int i = 0; i < numCloudlet; i++) {
			Cloudlet cloudlet=new Cloudlet(i, length[i], pesNumber, fileSize, outputSize, model, model, model);
			cloudlet.setUserId(brokerGreedyId);
			cloudletListGreedy.add(cloudlet);
		}
		brokerGreedy.submitCloudletList(cloudletListGreedy);
		
		System.out.println("本次实验vm个数为"+numVm+"，它们的mips分别为");
		for(int i=0;i<numVm;i++){
			System.out.print(mips[i]+"    ");
			if(i%5==4)System.out.println();
		}
		
		System.out.println("本次实验cloudlet个数为"+numCloudlet+"，它们的length分别为");
		for(int i=0;i<numCloudlet;i++){
			System.out.print(length[i]+"    ");
			if(i%5==4)System.out.println();
		}
		System.out.println();
		long startTime = System.currentTimeMillis();
		brokerACO.bind(10, 1000);
		long middleTime = System.currentTimeMillis();
		System.out.println();
		brokerGreedy.bindCloudletsToVmsTimeAwared();
		long endTime = System.currentTimeMillis();
		
		float acoTime = (middleTime - startTime) / 1000F; 
		System.out.println("蚁群算法运行时间："+ Float.toString(acoTime)+"s");
		float greedyTime = (endTime - middleTime) / 1000F; 
		System.out.println("贪心算法运行时间："+ Float.toString(greedyTime)+"s");
		
//		CloudSim.startSimulation();
//		CloudSim.stopSimulation();
//		
//		List<Cloudlet> newListACO=brokerACO.getCloudletReceivedList();
//		printCloudletList(newListACO);
//		List<Cloudlet> newListGreedy=brokerGreedy.getCloudletReceivedList();
//		printCloudletList(newListGreedy);
//		Log.printLine("TestExample finished!");
	}
	
	private static void printCloudletList(List<Cloudlet> list) {
		int size=list.size();
		Cloudlet cloudlet;
		String indent="    ";
		Log.printLine();
		Log.printLine("========OUTPUT========");
		Log.printLine("Cloudlet ID"+indent+"STATUS"+indent+"Datacenter ID"+indent+"VM ID"+indent+"Time"+indent+"Start Time"+indent+"Finish Time");
		DecimalFormat dft=new DecimalFormat("###.##");
		for(int i=0;i<size;i++){
			cloudlet=list.get(i);
			Log.print(indent+cloudlet.getCloudletId()+indent+indent);
			if (cloudlet.getCloudletStatus()==Cloudlet.SUCCESS) {
				Log.print("SUCESSS");
				Log.printLine(indent+indent+cloudlet.getResourceId()+indent+indent+indent
						+cloudlet.getVmId()+indent+indent+dft.format(cloudlet.getActualCPUTime())+
						indent+indent+dft.format(cloudlet.getExecStartTime())+
						indent+indent+dft.format(cloudlet.getFinishTime()));
				
			}
		}
		
	}

	/**
	 * 创造随机数
	 * @param num 随机数的数目
	 * @param max 随机数的上限
	 * @param min 随机数的下限
	 */
	public static int[] random(int num,int max,int min){
		int[] list;
		list = new int[num];
		for(int i=0;i<num;i++){
			list[i]=(int) (Math.random()*(max-min+1)+min);
		}
		return list;
		
	}
	
	private static DatacenterBrokerACO createBrokerACO(){
		DatacenterBrokerACO broker=null;
		try {
			broker=new DatacenterBrokerACO("BrokerACO");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return broker;
	}
	
	private static DatacenterBrokerGreedy createBrokerGreedy(){
		DatacenterBrokerGreedy broker=null;
		try {
			broker=new DatacenterBrokerGreedy("BrokerGreedy");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return broker;
	}

}
