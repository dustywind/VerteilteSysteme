

public class VSInvocationHandler implements InvocationHandler, Serializable
{   	
	private VSRemoteReference remoteReference;

	public VSInvocationHandler(VSRemoteReference remoteReference)
	{
		this.remoteReference = remoteReference;
	}


	public Object invoke(Object proxy , Method method, Object[] args) throws Throwable
	{

	}



}
