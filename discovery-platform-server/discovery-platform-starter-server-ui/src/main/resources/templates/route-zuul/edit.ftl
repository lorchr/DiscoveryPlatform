<@compress single_line=true>
    <!DOCTYPE html>
    <html lang="zh-CN">
    <head>
        <#include "../common/layui.ftl">
    </head>
    <body>

    <div class="layui-form" lay-filter="layuiadmin-form-admin" id="layuiadmin-form-admin" style="padding: 20px 30px 0 0;">

        <div class="layui-form-item">
            <label class="layui-form-label">网关名称</label>
            <div class="layui-input-inline" style="width: 740px">
                <input type="text" readonly="readonly" id="portalName" name="portalName" lay-verify="required" class="layui-input layui-disabled" value="${route.portalName}">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">服务名称</label>
            <div class="layui-input-inline" style="width: 740px">
                <select id="serviceId" name="serviceId" lay-filter="serviceId" autocomplete="off" lay-verify="required"
                        class="layui-select" lay-search>
                    <option value="">请选择服务名称</option>
                    <#list serviceNames as serviceName>
                        <option value="${serviceName}" ${(serviceName==route.serviceId)?string('selected="selected"','')}>${serviceName}</option>
                    </#list>
                </select>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">匹配路径</label>
            <div class="layui-input-inline" style="width: 740px">
                <input type="text" id="path" name="path" value="${route.path}" lay-verify="required" class="layui-input" placeholder="请输入匹配路径" lay-verify="required" autocomplete="off">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">转发路径</label>
            <div class="layui-input-inline">
                <input type="text" name="url" value="${route.url}" class="layui-input" style="width: 740px" placeholder="请输入转发地址" autocomplete="off">
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">是否过滤</label>
            <div class="layui-input-block">
                <input type="radio" name="stripPrefix" value="true" title="是" ${(route.stripPrefix) ? string('checked','')}>
                <input type="radio" name="stripPrefix" value="false" title="否" ${(!route.stripPrefix) ? string('checked','')}>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">是否重试</label>
            <div class="layui-input-block">
                <input type="radio" name="retryable" value="true" title="是" ${(route.retryable) ? string('checked','')}>
                <input type="radio" name="retryable" value="false" title="否" ${(!route.retryable) ? string('checked','')}>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">过滤敏感头</label>
            <div class="layui-input-block" style="width: 740px">
                <textarea id="sensitiveHeaders" name="sensitiveHeaders" class="layui-input" autocomplete="off" placeholder="请输入敏感请求头（使用逗号分隔）。 例如：&#13;Cookie,Set-Cookie,Authorization" style="height:100px;resize: none">${route.sensitiveHeaders}</textarea>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">是否自定义敏感头</label>
            <div class="layui-input-block">
                <input type="radio" name="customSensitiveHeaders" value="true" title="启用" ${(route.customSensitiveHeaders) ? string('checked','')}>
                <input type="radio" name="customSensitiveHeaders" value="false" title="禁用" ${(!route.customSensitiveHeaders) ? string('checked','')}>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">是否启用</label>
            <div class="layui-input-block">
                <input type="radio" name="enableFlag" value="true" title="启用" ${(route.enableFlag) ? string('checked','')}>
                <input type="radio" name="enableFlag" value="false" title="禁用" ${(!route.enableFlag) ? string('checked','')}>
            </div>
        </div>

        <div class="layui-form-item">
            <label class="layui-form-label">路由描述</label>
            <div class="layui-input-inline" style="width: 740px">
                <input type="text" id="description" name="description" class="layui-input" 
                       placeholder="请输入该条路由的描述信息" autocomplete="off" value=${route.description}>
            </div>
        </div>

        <div class="layui-form-item layui-hide">
            <input type="button" lay-submit lay-filter="btn_confirm" id="btn_confirm" value="确认">
        </div>
    </div>
    <script>
        layui.config({base: '../../..${ctx}/layuiadmin/'}).extend({index: 'lib/index'}).use(['index', 'form'], function () {
            const form = layui.form, $ = layui.$;

            form.on('select(serviceId)', function (data) {
                const text = data.elem[data.elem.selectedIndex].text;
                $("#path").val('/' + text + '/**');
                form.render();
                $("#description").focus();
            });

            <#if (gatewayNames?size==1) >
            $('#portalName option:eq(1)').attr('selected', 'selected');
            layui.form.render('select');
            </#if>

            <#if (serviceNames?size==1) >
            $('#serviceId option:eq(1)').attr('selected', 'selected');
            layui.form.render('select');

            const text = $('#serviceId').next().find("input").val();
            $("#path").val('/' + text + '/**');
            form.render();
            </#if>

        });
    </script>
    </body>
    </html>
</@compress>