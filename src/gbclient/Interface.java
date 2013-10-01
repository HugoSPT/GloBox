package gbclient;


import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.Theater;
import domain.TheaterSeats;

/**
 * Servlet implementation class Interface
 */
@WebServlet("/Interface")
public class Interface extends HttpServlet {

	private GBClient client;
	private final String COLUMNS = "ABCDEFGHIJKLMNOPQRSTUVXYWZ";


	//0 - Zonas; 1 - Teatros; 2 - Seats; 3 - Confirmation
	private int page;
	private final int MAX_CLIENTS = 10000000;
	/**
	 * Default constructor. 
	 */
	public Interface() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		if(client != null && client.isConnected())
			client.disconect();
		int id = new Random().nextInt(MAX_CLIENTS);
		boolean connected = true;
		try {
			client = new GBClient(id);
		} catch (NotBoundException e) {
			connected = false;
		}
		page = 0;
		printHeader(out);
		if(connected)
			printChooseZone(out);
		else
			printServerOff(out);
		printFooter(out);
	}

	private void printHeader(PrintWriter out){
		out.println("<html>\n" +
				"<head><title>GloBox</title></head>\n" +
				"<body>\n" +
				"<center><H1>Welcome to GloBox</H1></center>\n<form name=\"page\" action=\"Interface\" method=\"post\">");
		changeToPage(out,0);
		out.println("<input type=\"submit\" value=\"Back Home\" /></form><center>");

	}

	private void changeToPage(PrintWriter out, int page){

		out.println("<input type=\"hidden\" name=\"page\" value=\"" + page + "\"/>");

	}

	private void printFooter(PrintWriter out){
		out.println ("</center></body></html>");
	}

	private void printChooseZone(PrintWriter out) {
		out.println("<form name=\"zones\" action=\"Interface\" method=\"post\">" +
				"<input type=\"radio\" name=\"zone\" value=\"1\" checked /> North <br/>" +
				"<input type=\"radio\" name=\"zone\" value=\"2\" /> Center <br/>" +
				"<input type=\"radio\" name=\"zone\" value=\"3\" /> South <br/>");
		changeToPage(out, 1);
		out.println("<input type=\"submit\" value=\"Choose\" /></form>");
	}

	private void printChooseTheater(PrintWriter out, int zone){
		LinkedList<Theater> theaters = null;
		try {
			theaters = client.getTheatersList(zone);
		} catch (RemoteException e) {
			printServerOff(out);
			return;
		}
		
		if(theaters == null){
			printServerOff(out);
			return;
		}
		
		out.println("<form name=\"theaters\" action=\"Interface\" method=\"post\">");
		for(Theater t : theaters)
			out.println("<input type=\"radio\" name=\"theater\" value=\""+ t.getId() +"\" checked/>" + t.getName() + " - "+ t.getMovie() + "</br>");

		changeToPage(out, 2);
		out.println("<input type=\"submit\" value=\"Confirm\" /></form>");
	}

	private void printChooseSeat(PrintWriter out, int theaterID) {
		out.println("<form name=\"seats\" action=\"Interface\" method=\"post\"><table>");
		Theater t;
		try {
			t = client.getTheater(theaterID);
			if(t != null){
				out.println("<p>Theater: " + t.getName()+"</p>");
				out.println("<p>Movie:" + t.getMovie()+"</p>");
			}
		} catch (RemoteException e) {
			//BUSY
			e.printStackTrace();
		}

		TheaterSeats seats = null;
		int reservation = -1;
		try {
			seats = client.requestTheaterSeats(theaterID);
			reservation = client.getReservation();
		} catch (RemoteException e) {
			printServerOff(out);
			return;
		}
		
		if(seats == null){
			printServerOff(out);
			return;
		}

		int line = reservation/40+1;
		int column = reservation%40+1;
		int value;
		for(int i = 0; i < 27; i++){
			out.println("<tr>");
			for(int j = 0; j <= 40; j++){
				value = (i-1)*40+j-1;
				if(i == 0){
					if(j == 0)
						out.print("<td width=\"20\" height=\"20\"align=\"center\" valign=\"middle\"></td>");
					else	
						out.print("<td width=\"20\" height=\"20\" align=\"center\" valign=\"middle\">"+j+"</td>");
				}
				else {
					if(j == 0)
						out.print("<td width=\"20\" height=\"20\" align=\"center\" valign=\"middle\">"+COLUMNS.charAt(i-1)+"</td>");
					else{
						out.print("<td width=\"20\" height=\"20\" align=\"center\" valign=\"middle\"");
						char seat = seats.getSeatStatus(i-1, j-1);
						if(seat == 'O')
							out.print("style=\"background-color:#F00\">");
						else if (seat == 'L')
							out.print("style=\"background-color:#0F0\"><input type=\"radio\" name=\"seat\" value=\""+ value +"\"/>");
						else if(line == i && j == column)
							out.print("style=\"background-color:#FF0\"><input type=\"radio\" name=\"seat\" value=\""+ value +"\" checked/>");
						else
							out.print("style=\"background-color:#FF0\">");
						out.print("</td>");
					}
				}
			}
			out.println("</tr>");
		}
		changeToPage(out, 3);
		out.println("</table><input type=\"hidden\" name=\"theater\" value=\"" + theaterID + "\"/>" +
				"<input type=\"submit\" value=\"Reserve!\"/></form>");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		this.page = Integer.parseInt(request.getParameter("page"));
		printHeader(out);

		//Alterar switch para se ele n estiver connectado
		switch(page){
		case 0:
			if(client != null && client.isConnected()){
				client.cancelRequest();
				printChooseZone(out);
			}
			else
				printConnectionTimout(out);
			break;
		case 1:
			if(client.isConnected()){
				int zone = Integer.parseInt(request.getParameter("zone"));
				printChooseTheater(out, zone);
			}
			else
				printConnectionTimout(out);
			break;
		case 2:
			if(client.isConnected()){
				int theater = Integer.parseInt(request.getParameter("theater"));
				printChooseSeat(out, theater);
			}
			else
				printConnectionTimout(out);
			break;
		case 3:
			if(client.isConnected()){
				if(request.getParameter("seat") != null)
					printConfirmation(out, Integer.parseInt(request.getParameter("seat")), Integer.parseInt(request.getParameter("theater")));
				else
					out.println("<p>A Sala esta cheia! Impossivel reservar lugar.</p>");
			}
			else
				printConnectionTimout(out);
			break;
		case 4:
			//Reload
			boolean connected = true;
			try {
				this.client = new GBClient((this.client == null) ? new Random().nextInt(MAX_CLIENTS) : this.client.getID());
			} catch (NotBoundException e) {
				connected = false;
			}
			//Create new client
			//choose zone
			if(connected)
				printChooseZone(out);
			else
				printServerOff(out);
			break;
		default: out.println("404 - Page Not Found!"); break;
		}
		printFooter(out);

	}

	private void printConnectionTimout(PrintWriter out) {
		out.println("Connection Timout!");
		out.println("<form name=\"reload\" action=\"Interface\" method=\"post\">");
		changeToPage(out, 4);
		out.println("<input type=\"submit\" value=\"Reload!\"/></form>");
	}

	private void printServerOff(PrintWriter out) {
		out.println("Server is Off or Busy! Try again later.");
		out.println("<form name=\"reload\" action=\"Interface\" method=\"post\">");
		changeToPage(out, 4);
		out.println("<input type=\"submit\" value=\"Reload!\"/></form>");
	}

	private void printConfirmation(PrintWriter out, int seat, int theaterID) {

		try {
			if(client.setReservation(theaterID, seat)){
				if(client.reserve(theaterID))
					out.println("<p>Reserva feita com successo. Lugar: " + COLUMNS.charAt(seat/40) + "" + (seat%40 +1) + ".</p>");
				else
					out.println("<p>N‹o foi possivel efectuar a reserva.</p>");
			} else
				out.println("<p>Lugar " + COLUMNS.charAt(seat/40) + "" + (seat%40 +1) + " Ocupado.</p>");
		} catch (RemoteException e) {
			printServerOff(out);
		}


	}

}
