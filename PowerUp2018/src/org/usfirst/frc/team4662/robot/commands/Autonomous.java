package org.usfirst.frc.team4662.robot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.String;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class Autonomous extends CommandGroup {

	
	private Document m_patternAndCommandDoc;
	private XPath m_xPath;
	private boolean m_isDashboardTest; 
	private final String m_strStratPoFileName = "/home/lvuser/Autonomous/stratpo.txt";
	private final String m_strPatternxmlFilename = "/home/lvuser/Autonomous/AutoXMLForFIRSTPowerUp.xml";
	private String m_strStratPo;
	private String m_strPattern;
	private double m_dSpeed;
	
    public Autonomous() {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    	m_dSpeed = .75;
    	try {
    		System.out.println("In Auto Constructorxxx: m_strPatternxmlFilename" + m_strPatternxmlFilename);
    		File patternAndCommandFile = new File(m_strPatternxmlFilename);
    		System.out.println("In Auto Constructorxxx: patternAndCommandFile" + patternAndCommandFile);
    		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder dBuilder;
    		
    		dBuilder = dbFactory.newDocumentBuilder();
    		
    		m_patternAndCommandDoc = dBuilder.parse(patternAndCommandFile);
    		m_patternAndCommandDoc.getDocumentElement().normalize();
    		System.out.println("In Auto Constructor: pattern=" + m_patternAndCommandDoc);
    		m_xPath = XPathFactory.newInstance().newXPath();
    		
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	SmartDashboard.putString("AutoCommandGroup", "Start");
    	
    	
    	
		SmartDashboard.putString("Field Layout", "LL");
    }
    private String readFile (String file) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String	       line = null;
		StringBuilder   stringBuilder = new StringBuilder();
		boolean bFirstTime = true;
		while( ( line = reader.readLine())!= null){
			line = line.trim();
			if (bFirstTime == true) {
				bFirstTime = false;
			} else {
				stringBuilder.append( "|" );
			}
			stringBuilder.append( line );
		}
		reader.close();
		return stringBuilder.toString();
	}
    
    public void initialize() {
    	
    }
    public void addCommands() {
    	String gameData = DriverStation.getInstance().getGameSpecificMessage();
    	SmartDashboard.putString("Field Layout", gameData);
    	String strCommand;
    	String strCommandValue;
   
		try {
			m_strStratPo = readFile(m_strStratPoFileName);
			SmartDashboard.putString("StratPo Value", m_strStratPo);
			
	    	String searchExpr = "//stratpo[@name=\"" + m_strStratPo + "\"]//Pattern[@fieldlayout=\"" + gameData.substring(0, 2)  + "\"]/text()";
	    	SmartDashboard.putString("searchexpr", searchExpr);
	    	System.out.println("addCommands:" + searchExpr);
			System.out.println("addCommands:" + m_patternAndCommandDoc);
			System.out.println("addCommands:" + m_xPath);
			NodeList nodeList = (NodeList) m_xPath.compile(searchExpr).evaluate(m_patternAndCommandDoc, XPathConstants.NODESET);
			
			if (nodeList.getLength()==1) {
				m_strPattern = nodeList.item(0).getNodeValue();
				SmartDashboard.putString("Pattern", m_strPattern);
				
			} else {
				m_strPattern = "Auto2";
				SmartDashboard.putString("Pattern", "no Pattern found");
			}
			//PatternCommands[@name="Pos4Rsc"]//Command
			searchExpr = "//PatternCommands[@name=\"" + m_strPattern + "\"]";
			SmartDashboard.putString("searchexpr2", searchExpr);
			nodeList = (NodeList) m_xPath.compile(searchExpr).evaluate(m_patternAndCommandDoc, XPathConstants.NODESET);
			SmartDashboard.putNumber("numCommandPatternsFound", nodeList.getLength());
			if (nodeList.getLength() == 1) {
				Node node;
				NodeList nlCommands;
				
				node = nodeList.item(0);
				nlCommands = node.getChildNodes();
				SmartDashboard.putNumber("numCommandsFound", nlCommands.getLength());
				for(int i = 0;i<nlCommands.getLength();i++) {
					Node nNode;
					Element eElement;
					
					nNode = nlCommands.item(i);
					if (nNode.getNodeType()==Node.ELEMENT_NODE) {
						eElement = (Element) nNode;
						strCommand = eElement.getAttribute("name");
						//strCommandValue = "CommandValue";
						strCommandValue = eElement.getTextContent();
						SmartDashboard.putString("Command " + i,strCommand + ":" + strCommandValue);
						addCommand(strCommand, strCommandValue);
						
					} else {
						System.out.println(nNode.getNodeType());
					}
					
					
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }
    private void addCommand(String command, String commandValue) {
    	switch (command) {
    	case "Forward":
    		addSequential( new DriveDistancePID( Double.valueOf(commandValue), m_dSpeed));
    		break;
    	case "RotateR":
    		addSequential( new TurnAnglePID( Double.valueOf(commandValue)));
    		break;
    	case "UnloadToSw":
    		//addSequential( new TimedLift(2));
    		addSequential( new PutPCubeDown());
    		break;
    	case "UnloadToSc":
    		//addSequential( new TimedLift(2));
    		addSequential( new PutPCubeDown());
    		break;
    	case "MoveLiftToTarget":
    		addParallel( new MoveLiftToTarget(Double.valueOf(commandValue)));
    		break;
    	case "MoveLiftToTargetTimed":
    		addParallel( new TimedLift(Double.valueOf(commandValue)));
    		break;
    	case "TiltToBottom":
    		addSequential( new TiltToBottom(Double.valueOf(commandValue)));
    		break;
    	case "GrabOpenTimed":
    		addSequential( new GrabOpenTimed(Double.valueOf(commandValue)));
    		break;
    	case "RotateL":
    		addSequential( new TurnAnglePID( -Double.valueOf(commandValue)));
    		break;
    	case "Speed":
    		m_dSpeed = Math.min(1, Double.valueOf(commandValue));
    		break;
    	case "TiltToVertical" :
    		addSequential( new TiltToVertical(2));
    		break;
		case "TurnLeftTimed" :
    		addSequential( new TurnLeftTimed(Double.valueOf(commandValue)));
    		break;
    	case "TurnRightTimed" :
    		System.out.println("InTurnRightTimed");
    		addSequential( new TurnRightTimed(Double.valueOf(commandValue)));
    		break;
    	default:
    		
    	}
    }
}

