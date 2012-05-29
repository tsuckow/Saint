package net.codingwell.saint

import java.io._
import gnu.io._
import net.codingwell._

object Main {

   def main(args: Array[String]):Unit = {

      //println(java.library.path)
	  
	  LibraryPathManipulator.addDir("libs/rxtx")
	  
	  listPorts()
	  
	  val port = findPort("COM12")
	  val stream = port.getOutputStream()
	  
	  val vkm = new VirtualKeyMouse( stream )
	  
	  
	  vkm.mouseTo( 700,700 )
	  vkm.mouseTo( 1700,700 )
	  vkm.mouseTo( 1700,1000 )
	  vkm.mouseTo( 10,10 )
	  
	  port.close()
   }
   
   def findPort(name:String):CommPort = {
        val portEnum = CommPortIdentifier.getPortIdentifiers()
        while ( portEnum.hasMoreElements() ) 
        {
            val portIdentifier = portEnum.nextElement().asInstanceOf[CommPortIdentifier]
			
			if( portIdentifier.getName() == name )
				try {
					return portIdentifier.open("CommUtil", 50)
				} catch {
					case _ => throw new IOException()
				}
        }

		throw new FileNotFoundException()
    }
   
   def listPorts():Unit = {
        var portEnum = CommPortIdentifier.getPortIdentifiers()
        while ( portEnum.hasMoreElements() ) 
        {
            var portIdentifier = portEnum.nextElement().asInstanceOf[CommPortIdentifier]
            println(portIdentifier.getName()  +  " - " +  portIdentifier.getPortType().toString() )
        }        
    }
}

object VirtualKeyMouse {
	import java.awt._
	
	val mouseMoveDelay = 1
	
	def mousePosition():Point = {
		MouseInfo.getPointerInfo().getLocation()
	}
	
	private def legInt[U](lhs:Int,rhs:Int,l:U,e:U,g:U) = {
		if( lhs < rhs )
		{
			l
		}
		else if( lhs > rhs )
		{
			g
		}
		else
		{
			e
		}
	}
}

class VirtualKeyMouse( val stream:OutputStream ) {
	import java.awt.Point
	import VirtualKeyMouse._
	
	def mouseTo(x:Int,y:Int):Unit = {
		val target = new Point(x,y)
		
		var point = mousePosition()
		
		/*var lastPoint:Point = null*/
		
		var failsafe = 5000;
		
		while( point != target /*&& point != lastPoint*/ )
		{
			/*lastPoint = point*/
			
			stream.write( Array[Byte]('m',legInt(target.x,point.x,-1,0,1),legInt(target.y,point.y,-1,0,1)) )
			
			if( failsafe == 0 ) return
			failsafe = failsafe - 1
			
			Thread.sleep( mouseMoveDelay )
			
			point = mousePosition()
		}
	}
}