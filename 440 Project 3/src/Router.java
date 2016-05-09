/**
 * @author elvinw
 * @date 7/20/2015
 * Router object
 */

import java.util.LinkedList;

/*
 * This runnable routes packets as they traverse the network.
 */
class Router implements Runnable {
	private LinkedList<Packet> list = new LinkedList<Packet>();
	private int routes[];
	private Router routers[];
	private int routerNum;
	private boolean end = false;

	Router(int rts[], Router rtrs[], int num) {
		routes = rts;
		routers = rtrs;
		routerNum = num;
	}

	/*
	 * Add a packet to this router. routing class will call the addwork method
	 * while passing a packet. this packet will be enqueued to the LinkedList
	 * and then we will notify the thread that there is work to be done
	 */
	public synchronized void addWork(Packet p) {
		list.add(p);
		notify();
	}

	/*
	 * End the thread, once no more packets are outstanding.
	 */
	public synchronized void end() {
		end = true;
		notify();
	}

	public synchronized void networkEmpty() {

	}

	/*
	 * Process packets. Add some details on how this works.
	 */
	public void run() {
		Packet p = null;

		// keep checking to see if the queue has some contents. if we haven't
		// put a packet it will keep looping
		while (true) {
			try {
				// if list is empty but end is still false
				while (list.isEmpty() && (!end)) {
					// this router will wait for packets
					synchronized (this) {
						wait();
					}
				}
				// if list is empty and end is true, we return
				if (list.isEmpty() && (end)) {
					return;
				}

				// take out a packet for this router
				synchronized (this) {
					p = list.remove();
				}
			} catch (InterruptedException e) {
				System.out.println("error");
			}

			// records the router number for current packet
			p.Record(routerNum);

			// forwarding the current packet to the correct router for
			// processing of the packet

			if (routerNum != p.getDestination()) {
				int fwd = routes[p.getDestination()];
				routers[fwd].addWork(p);
			}

		}
	}
}
