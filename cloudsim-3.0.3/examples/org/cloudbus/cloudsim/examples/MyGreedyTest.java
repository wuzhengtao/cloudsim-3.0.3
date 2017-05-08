package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.DatacenterBrokerGreedy;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

public class MyGreedyTest {
	//一个任务列表
	private static List<Cloudlet> cloudletList;
	private static int numCloudlet = 10;
	
	//虚拟机列表
	private static List<Vm> vmlist;
	private static int numVm = 5;
	
	

	public static void main(String[] args) {
		Log.printLine("Starting GreedyExample...");
		int num_user=1;
		Calendar calendar=Calendar.getInstance();
		boolean trace_flag=false;
		
		CloudSim.init(num_user, calendar, trace_flag);
		
		DatacenterBrokerGreedy broker = createBroker();
		int brokerId = broker.getId();
		
		int vmid=0;
		int []mipss=new int[]{278,289,132,209,286};
//		int []mipss=new int[numVm];
//		for(int i=0;i<numVm;i++){
//			mipss[i]=(int)(Math.random()*200+100);
//		}
		long size=10000;
		int ram=2048;
		long bw=10000;
		int pesNumber=1;
		String vmm="xen";
		
//		开始添加Vm
		vmlist = new ArrayList<Vm>();
		for(int i=0;i<numVm;i++){
			vmlist.add(new Vm(vmid,brokerId,mipss[i], pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared()));
			vmid++;
		}
		broker.submitVmList(vmlist);
		
		int id=0;
		long[] lengths=new long[]{19365,49809,30218,44157,16754,18336,20045,31493,30727,31017};
		long fileSize=300;
		long outputSize=300;
		UtilizationModel model=new UtilizationModelFull();
		
		cloudletList=new ArrayList<Cloudlet>();
		for (int i = 0; i < numCloudlet; i++) {
			Cloudlet cloudlet=new Cloudlet(id, lengths[i], pesNumber, fileSize, outputSize, model, model, model);
			cloudlet.setUserId(brokerId);
			cloudletList.add(cloudlet);
			id++;
		}
		broker.submitCloudletList(cloudletList);
		
		broker.bindCloudletsToVmsTimeAwared();
		
		CloudSim.startSimulation();
		
		List<Cloudlet> newList=broker.getCloudletReceivedList();
		CloudSim.stopSimulation();
		printCloudletList(newList);
		Log.printLine("GreedyExample finished!");

	}



	private static DatacenterBrokerGreedy createBroker() {
		DatacenterBrokerGreedy broker = null;
		try {
			broker = new DatacenterBrokerGreedy("broker");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return broker;
	}
	
	private static void printCloudletList(List<Cloudlet> list){
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

}
