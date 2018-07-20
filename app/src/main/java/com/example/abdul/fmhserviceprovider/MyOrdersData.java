package com.example.abdul.fmhserviceprovider;

public class MyOrdersData  {
	private String orderID;
	private String orderStatus;
	private String customerName;
	private String customerAddress;
	private String customerPhone;
	private String orderTotal;
	private String orderTime;
	private String orderDate;
	private String orderDescription;
	
	public MyOrdersData(String orderID, String orderStatus, String customerName, String customerAddress, String customerPhone, String orderTotal, String orderTime, String orderDate, String orderDescription) {
		this.orderID = orderID;
		this.orderStatus = orderStatus;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
		this.customerPhone = customerPhone;
		this.orderTotal = orderTotal;
		this.orderTime = orderTime;
		this.orderDate = orderDate;
		this.orderDescription = orderDescription;
	}
	
	public String getOrderID() {
		return orderID;
	}
	
	public String getOrderStatus() {
		return orderStatus;
	}
	
	public String getOrderTotal() {
		return orderTotal;
	}
	
	public String getOrderTime() {
		return orderTime;
	}
	
	public String getOrderDate() {
		return orderDate;
	}
	
	public String getOrderDescription() {
		return orderDescription;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public String getCustomerAddress() {
		return customerAddress;
	}
	
	public String getCustomerPhone() {
		return customerPhone;
	}
}

