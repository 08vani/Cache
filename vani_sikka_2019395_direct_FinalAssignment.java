package Cache;

import java.util.*;





public class vani_sikka_2019395_direct_FinalAssignment
{
	static class cache_line
	{
		/*It is a class that represents every line in the cache. It has two parts to it .
		 * The tag part of the class is of string type storing the tag address present at 
		 * that cache line. The second part is block i.e. an array strings where each index in 
		 * array act as a word number and the data stored in that index is the data stored at 
		 * that word number.    
		 * 
		 */
		String tag;
		String[] block;
		cache_line(int bl_size)
		{
			tag="-";
			block=new String[bl_size];
		}
	}
	static Map<String,String> memory=new HashMap<>();//first string variable stores the address to which the user is written and second string is the data stored in that address but whenever you enter data ensure that string has less than or equal to 8 but not more than that. 
	public static void main(String[] args) 
	{
		/*This function takes the cache specification as input. 
		 * The then initialize the cache with those inputs. 
		 * Then the function takes input from the user about their write or read commands.
		 *  Depending upon what command the user has entered it calls write_cache
		 *   or read_cache functions. It takes commands continuously till the time user writes “STOP”.
		 */
		Scanner in= new Scanner (System.in);
		System.out.println("PLEASE ENTER PHYSICAL ADDRESS LENGTH:BLOCK SIZE:CACHE LINE");
		String inc=in.nextLine();
		String[] i=inc.split(":");
		int memory_size=Integer.valueOf(i[0]);//Exponent of main memory which is in byte 
		int block_size=Integer.valueOf(i[1]);//in bytes(word size)i.e block_size addresses in a block of one word each hence is the number of addresses in a block
		int cache_size=Integer.valueOf(i[2]);//in bytes(word size)i.e cache_size addresses in cache of one word each hence is the number of addresses in cache
		if(block_size>Math.pow(2,memory_size) || cache_size>Math.pow(2,memory_size))
		{
			if(block_size>Math.pow(2,memory_size))
			{
				System.out.println("ERROR:BLOCK SIZE CAN NOT BE GREATOR THAN MEMORY SIZE");
				return;
			}
			if(cache_size>Math.pow(2,memory_size))
			{
				System.out.println("ERROR:CACHE SIZE CAN NOT BE GREATOR THAN MEMORY SIZE");
				return;
			}
		}
		int p=get_power(cache_size);//exponent of cache size which is in byte
		int n=memory_size;//Exponent of main memory which is in byte 
		int w=get_power(block_size);//exponent of block size which is in byte 
		int total_blocks=(int) Math.pow(2, (n-w));
		cache_line[] cache=new cache_line[cache_size];
		for(int j=0;j<cache.length;j++)
		{
			cache[j]=new cache_line(block_size);
		}
		String str="";
		str=in.nextLine();//taking the address as inputs in case of loading and taking address and data as input in case of writing
		while(str.equals("STOP")==false)
		{
			
			String[] arr=str.split(":");
			
			if(arr.length==1)
			{
				//System.out.println(arr[0]);
				read_cache(arr[0],n,p,w,memory_size,block_size,cache_size,total_blocks,cache);
				print_cache(cache);
			}
			else 
			{
				//System.out.println(arr[0]+" "+arr[1]);
				write_cache(arr[0],arr[1],n,p,w,memory_size,block_size,cache_size,total_blocks,cache);
				print_cache(cache);
			}
			str=in.nextLine();//taking the address as inputs in case of loading and taking address and data as input in case of writing
		}
		
		
		
	
	}
	public static void print_cache(cache_line[] cache)
	{
		/*Function to print the entire cache.
		 * 
		 */
		System.out.println("TAG     BLOCK");
		for(int i=0;i<cache.length;i++)
		{
			System.out.print(cache[i].tag+"     ");
			for(int j=0;j<cache[i].block.length;j++)
				System.out.print(cache[i].block[j]+"  ");
			System.out.println();
		}
	}
	private static void write_cache(String adrs, String data,int n,int p,int w,int memory_size,int block_size,int cache_size,int total_blocks,cache_line[] cache) 
	{
		/*This function check if the data to be written is less than equal to eight bits;
		 *if not then it displays error. Then the function slices the input address
		 *according to the mapping rules. After slicing it searches in the cache using 
		 *mapping specific technique. If the particular address is present in the cache
		 *then it declares “HIT” and displays the contents of the entire cache line where 
		 *the address is found after writing in the cache. If the address is not present 
		 *in the cache insert_block function is called to insert the block which has that address 
		 *and it also declares a “MISS”.
		 */
		if(data.length()>8)
		{
			System.out.println("Data entered has exceeded the word length,hence can't write the data");
			return;
		}
		String block_no=adrs.substring(0, (n-w));
		String word_no=adrs.substring(((n-w)),adrs.length());
		String tag=block_no.substring(0,((n-w-p)));//tag is the block no to which the address belong
		String cache_line=block_no.substring((n-w-p),block_no.length());//it is the expected index where the tag should be there as cache_line assigned to a tag is with formula block_size mod cache_size.Since this formula can map many block to a single cache_line hence we have to check that whether the tag or block address in the input memory address is same as the tag past in the cache line
		int cl=binary_to_decimal(cache_line);
		if(cache[cl].tag.equals("-")==false && cache[cl].tag.equals(tag) )
		{
			System.out.println("HIT");
			int word_index=binary_to_decimal(word_no);
			cache[cl].block[word_index]=data;
			memory.put(adrs,data);
		}
		else
		{
			System.out.println("MISS");
			memory.put(adrs,data);
			insert_block(word_no,block_no,cache,tag,cl,block_size,n,w,cache_size);
			System.out.println("Block to be inserted: "+block_no);
		}
		
		
	}
	public static void read_cache(String adrs,int n,int p,int w,int memory_size,int block_size,int cache_size,int total_blocks,cache_line[] cache)
	{
		/*Then the function slices the input address according to the mapping rules. 
		 * After slicing it searches in the cache using mapping specific technique. 
		 * If the particular address is present in the cache then it declares “HIT” 
		 * and displays the contents stored in the address entered by the user. 
		 * If the address is not present in the cache insert_block function 
		 * is called to insert the block which has that address and it also declares a “MISS”. 
		 * Once the block is inserted then it displays the content stored in the given address.      
		 */
		String block_no=adrs.substring(0, ((n-w)));
		String word_no=adrs.substring(((n-w)),adrs.length());
		String tag=block_no.substring(0,((n-w-p)));//tag is the block no to which the address belong
		String cache_line=block_no.substring((n-w-p),block_no.length());//it is the expected index where the tag should be there as cache_line assigned to a tag is with formula block_size mod cache_size.Since this formula can map many block to a single cache_line hence we have to check that whether the tag or block address in the input memory address is same as the tag past in the cache line
		int cl=binary_to_decimal(cache_line);
		//System.out.println(cl+":"+cache_line+"*"+tag+"*"+word_no+"*"+block_no);
		if(cache[cl].tag.equals("-")==false &&cache[cl].tag.equals(tag))
		{
			System.out.println("HIT");
			int word_index=binary_to_decimal(word_no);
			System.out.println("DATA A GIVEN ADDRESS: ");
			System.out.println(cache[cl].block[word_index]);
		}
		else
		{
			System.out.println("MISS");
			System.out.println("DATA A GIVEN ADDRESS: ");
			read_main_memory(adrs,word_no,block_no,cache,tag,cl,block_size,n,w,cache_size);
			System.out.println("Block to be inserted: "+block_no);
		}
		
		
	}
	private static void read_main_memory(String adrs,String wd_no,String bl_no,cache_line[] cache,String tag,int cl,int block_size,int n,int w,int cache_size) 
	{
		/*This function is called when there is miss and a new block is to be inserted.
		 *  Hence it displays the contents stored in the memory address if there 
		 *  is no address stored it displays null. Then it call the insert block function 
		 *  to insert block in the cache.
		 * 
		 */
		if(memory.containsKey(adrs)==false)
		{
			System.out.println("null");
			
			
		}
		else
		{
			System.out.println(memory.get(adrs));
		}
		
		insert_block(wd_no,bl_no,cache,tag,cl,block_size,n,w,cache_size);
		
			
		
	}
	private static void insert_block(String wd_no,String bl_no,cache_line[] cache,String tag,int cl,int block_size,int n,int w,int cache_size) 
	{
		/*The function calls get address function to get all the address present in the block. 
		 * Then it iterates over all the function to see if any of the address has got updated
		 *  recently by searching in the memory HashMap. If any of the address at that block has 
		 *  got updated then the updated value is stored at that address and if not updated then 
		 *  null is stored. Finally, when all the addresses has got their respective data the block 
		 */
		String[] a=get_addresses(wd_no,bl_no);
		cache_line bl=new cache_line(block_size);
		bl.tag=tag;
		for(int i=0;i<a.length;i++)
		{
			String address_no=a[i].substring(((n-w)),a[i].length());
			int word_index=binary_to_decimal(address_no);
			if(memory.containsKey(a[i])==false)
			{
				bl.block[word_index]=null;
			}
			else
			{
				bl.block[word_index]=memory.get(a[i]);
			}
		}
		cache[cl]=bl;
		
	}
	private static String[] get_addresses(String wd_no, String bl_no) 
	{
		/*This function generates all the address that a block has. 
		 *It does by finding all the binary strings that are possible of length
		 * of the wd_no passed as the parameter. Then it makes an array if String 
		 * where all the permutations succeeding the block number passed as parameter 
		 * is stored. Hence this way the array contains all the address that a block has. 
		 * This array is returned by the function.
		 */
		int end=(int) Math.pow(2, wd_no.length());
		String[] ans=new String[end];
		for(int i=0;i<end;i++)
		{
			ans[i]=bl_no+decimal_to_binary(i,wd_no);
		}
		return ans;
	}
	public static int get_power(int number)
	{
		/*
		 * The function returns the exponent of 2 that 
		 * equals to the number passed.
		 */
		if(number==0)
			return 0;
		int power=0;
		while(number!=1)
		{
			if(number%2==0)
				power=power+1;
			number/=2;
		}
		return power;
	}
	public static int binary_to_decimal(String bin)
	{
		/*The function returns the binary string of the same length as the word 
		 * length of the passed integer.
		 * 
		 */
		int num=0;
		for(int i=0,j=bin.length()-1;j>=0;j--,i++)
		{
			int digit=Character. getNumericValue(bin.charAt(j));
			num=(int) (num+digit*Math.pow(2, i));
			
		}
		return num;
	}
	public static String decimal_to_binary(int num,String wd_no)
	{
		/*it returns the integer value of the binary string passed 
		 * as the parameter.
		 */
		String a="";
		while(num>1)
		{
			int r=num%2;
			a=a+Integer.toString(r);
			num=num/2;
		}
		a=a+num;
		String ans="";
		for(int i=a.length()-1;i>=0;i--)
		{
			ans=ans+a.charAt(i);
		}
		String aans="";
		boolean flag=false;
		if(ans.length()<wd_no.length())
		{
			flag=true;
			int diff=wd_no.length()-ans.length();
			for(int i=0;i<diff;i++)
				aans=aans+"0";
			aans=aans+ans;
		}
		if(flag)
			return aans;
		else
			return ans;
	
	}

}
