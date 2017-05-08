package org.cloudbus.cloudsim;

import java.util.Collections;
import java.util.Comparator;

public class DatacenterBrokerGreedy extends DatacenterBroker {

	public DatacenterBrokerGreedy(String name) throws Exception {
		super(name);
	}
	
	/**
	 * greedy algorithm based cloudlet scheduling
	 *贪心算法实现任务调度
	 */
	public void bindCloudletsToVmsTimeAwared() {
		int cloudletNum = cloudletList.size();
		int vmNum = vmList.size();
		double[][] time = new double[cloudletNum][vmNum];
		Collections.sort(cloudletList, new CloudletComparator());
		Collections.sort(vmList, new VmComparator());
		for (int i = 0; i < cloudletNum; i++) {
			/*
			 * 计算每一个cloudlet在每一个VM上运行的时间
			 * i表示cloudlet，j表示VM
			 */
			for (int j = 0; j < vmNum; j++) {
				time[i][j] = (double) cloudletList.get(i).getCloudletLength() / vmList.get(j).getMips();
//				 System.out.print(time[i][j]+" "); //For test
			}
//			 System.out.println(); //For test
		}
		double[] vmLoad = new double[vmNum];
		int[] vmTasks = new int[vmNum]; // The number of tasks running on the
										// specific vm
		double minLoad = 0;
		int idx = 0;
		// Allocate the first cloudlet to the fastest vm
		//将第一个cloudlet分配给最快的vm
		//将MI最大的cloudlet分配给MIPS最大的vm
		vmLoad[vmNum - 1] = time[0][vmNum - 1];
		vmTasks[vmNum - 1] = 1;
		System.out.println("0分配给：4");
		cloudletList.get(0).setVmId(vmList.get(vmNum - 1).getId());
		for (int i = 1; i < cloudletNum; i++) {
			minLoad = vmLoad[vmNum - 1] + time[i][vmNum - 1];
			idx = vmNum - 1;//找最合适的vm
			for (int j = vmNum - 2; j >= 0; j--) {
				if (vmLoad[j] == 0) {
					if (minLoad >= time[i][j])
						idx = j;
					break;
				}
				if (minLoad > vmLoad[j] + time[i][j]) {
					minLoad = vmLoad[j] + time[i][j];
					idx = j;
				}
				// Load balance
				else if (minLoad == vmLoad[j] + time[i][j] && vmTasks[j] < vmTasks[idx])
					idx = j;
			}
			vmLoad[idx] += time[i][idx];
			vmTasks[idx]++;
			cloudletList.get(i).setVmId(vmList.get(idx).getId());
			System.out.println(i+"分配给："+idx);
		}
	}

	// Cloudlet根据MI降序排列
	private class CloudletComparator implements Comparator<Cloudlet> {
		public int compare(Cloudlet c11, Cloudlet c12) {
			return (int) (c12.getCloudletLength() - c11.getCloudletLength());
		}
	}

	// Vm根据MIPS升序排列
	private class VmComparator implements Comparator<Vm> {
		public int compare(Vm vm1, Vm vm2) {
			return (int) (vm1.getMips() - vm2.getMips());
		}
	}

}
