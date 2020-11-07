package templates

const (
	Service = `
{{- with $s := .}}
package {{$s.JavaPackage}}

import kotlinx.coroutines.flow.Flow
import io.grpc.MethodDescriptor.generateFullMethodName
import ws.leap.kettle.grpc.ClientCalls
import ws.leap.kettle.grpc.ServerCalls

{{/**
 * <pre>
 * Test service that supports all call types.
 * </pre>
 */}}
@javax.annotation.Generated(
  value = ["by gRPC proto compiler (version 0.5.0)"],
  comments = "Source: {{.ProtoFile}}")
object {{$s.Name}}GrpcKt {
  const val SERVICE_NAME = "{{$s.ProtoName}}"

  // Static method descriptors that strictly reflect the proto.
  {{- range $i, $m := .Methods}}
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  val {{$m.FieldName}}: io.grpc.MethodDescriptor<{{$m.InputType}}, {{$m.OutputType}}> =
    io.grpc.MethodDescriptor.newBuilder<{{$m.InputType}}, {{$m.OutputType}}>()
      .setType(io.grpc.MethodDescriptor.MethodType.{{$m.GrpcMethodType}})
      .setFullMethodName(generateFullMethodName("{{$s.ProtoName}}", "{{$m.Name}}"))
      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller({{$m.InputType}}.getDefaultInstance()))
      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller({{$m.OutputType}}.getDefaultInstance()))
      .build()
  {{- end}}

  /**
   * Creates a new RX stub
   */
  fun newStub(client: ws.leap.kettle.http.HttpClient, address: java.net.URL): {{.Name}}Stub {
    return {{.Name}}Stub(client, address)
  }

  /**
   * Creates a new RX stub with call options
   */
  fun newStub(client: ws.leap.kettle.http.HttpClient, address: java.net.URL, callOptions: ws.leap.kettle.grpc.CallOptions): {{.Name}}Stub {
    return {{.Name}}Stub(client, address, callOptions)
  }

  {{/**
   * <pre>
   * Test service that supports all call types.
   * </pre>
   */}}
  interface {{$s.Name}} {
    {{range $i, $m := .Methods}}
    {{- /**
     * <pre>
     * One requestMore followed by one response.
     * The server returns the client payload as-is.
     * </pre>
     */ -}}
    suspend fun {{$m.JavaName}}(req: {{$m.FullInputType}}): {{$m.FullOutputType}}
    {{end}}
  }

  {{/**
   * <pre>
   * Test service that supports all call types.
   * </pre>
   */}}
  abstract class {{.Name}}ImplBase : ws.leap.kettle.grpc.BindableService, {{$s.Name}} {
    {{range $i, $m := .Methods}}
    {{- /**
     * <pre>
     * One requestMore followed by one response.
     * The server returns the client payload as-is.
     * </pre>
     */ -}}
    override suspend fun {{$m.JavaName}}(req: {{$m.FullInputType}}): {{$m.FullOutputType}} {
      return ServerCalls.{{$m.UnimplementedCall}}({{$m.FieldName}})
    }
    {{end}}
    override fun bindService(): ws.leap.kettle.grpc.ServerServiceDefinition {
      return ws.leap.kettle.grpc.ServerServiceDefinition(serviceDescriptor) {
        {{- range $i, $m := .Methods}}
        addMethod({{$m.FieldName}}, ServerCalls.{{$m.Call}}(::{{$m.JavaName}}))
        {{- end}}
      }
    }
  }

  {{/**
   * <pre>
   * Test service that supports all call types.
   * </pre>
   */}}
  class {{$s.Name}}Stub internal constructor(client: ws.leap.kettle.http.HttpClient, address: java.net.URL, callOptions: ws.leap.kettle.grpc.CallOptions = ws.leap.kettle.grpc.CallOptions())
    : ws.leap.kettle.grpc.AbstractStub<{{$s.Name}}Stub>(client, address, callOptions), {{$s.Name}} {

    override fun build(client: ws.leap.kettle.http.HttpClient, address: java.net.URL, callOptions: ws.leap.kettle.grpc.CallOptions): {{$s.Name}}Stub {
      return {{$s.Name}}Stub(client, address, callOptions)
    }

    {{range $i, $m := .Methods}}
    {{- /**
     * <pre>
     * One requestMore followed by one response.
     * The server returns the client payload as-is.
     * </pre>
     */ -}}
    override suspend fun {{$m.JavaName}}(req: {{$m.FullInputType}}): {{$m.FullOutputType}} {
      return ClientCalls.{{$m.Call}}(
        newCall({{$m.FieldName}}, callOptions), {{$m.CallParams}})
    }
    {{end}}
  }

  private class {{$s.Name}}DescriptorSupplier : io.grpc.protobuf.ProtoFileDescriptorSupplier {
    override fun getFileDescriptor(): com.google.protobuf.Descriptors.FileDescriptor {
      return {{$s.JavaPackage}}.{{$s.OuterClassName}}.getDescriptor()
    }
  }

  val serviceDescriptor: io.grpc.ServiceDescriptor by lazy {
    io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
      .setSchemaDescriptor({{$s.Name}}DescriptorSupplier())
      {{- range $i, $m := .Methods}}
      .addMethod({{$m.FieldName}})
      {{- end}}
      .build()
  }
}
{{- end}}
`
)
