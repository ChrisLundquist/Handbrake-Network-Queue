class ServerThread

  def initialize(client, queue)
    @client = client
    @queue = queue
  end

  def serve_client
    puts "Thread #{$$} serving client"
    command = @client.read()
    puts command
    case command 
    when Command::GET_JOB
      get_job()
    when Command::CHECKOUT_JOB
      checkout_job()
    when Command::COMPLETE_JOB
      complete_job()
    else
      STDERR.puts "unkown command"
    end
    @client.close
  end

  def get_job
    puts "client has requested a job"
    @client.puts(@job_queue.next_job().to_yaml)
  end

  def checkout_job
    puts "client has checked out a job"
  end

  def complete_job
    puts "client has completed a job"
  end

end
